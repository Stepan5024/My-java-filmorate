package ru.yandex.practicum.filmorate.repository.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
public class UserDbStorage implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO \"User\" (\"Email\", \"Login\", \"Name\", \"Birthday\") VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);

        // Сохранение друзей
        for (User userFriend : user.getFriends()) {
            addFriendToDb(userId, userFriend.getId());
        }

        log.info("Saved user id {}", userId);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        String sql = "DELETE FROM \"User\" WHERE \"ID\" = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public User updateUser(Long id, User user) {
        String sql = "UPDATE \"User\" SET \"Email\" = ?, \"Login\" = ?, \"Name\" = ?, \"Birthday\" = ? WHERE \"ID\" = ?";
        int rowsUpdated = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(),
                user.getName(), Date.valueOf(user.getBirthday()), id);
        if (rowsUpdated > 0) {
            String deleteFriendsSql = "DELETE FROM \"UserFriend\" WHERE \"UserID\" = ? OR \"FriendID\" = ?";
            jdbcTemplate.update(deleteFriendsSql, id, id);

            for (User userFriends : user.getFriends()) {
                addFriendToDb(id, userFriends.getId());
            }

            user.setId(id);

            return user;
        }
        return null;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM \"User\" WHERE \"ID\" = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{id}, new UserRowMapper());

        if (!users.isEmpty()) {
            User user = users.getFirst();
            user.setFriends(getUserFriends(id));
            return Optional.of(user);
        }

        return users.stream().findFirst();
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM \"User\"";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("ID"));
            user.setEmail(rs.getString("Email"));
            user.setLogin(rs.getString("Login"));
            user.setName(rs.getString("Name"));
            user.setBirthday(rs.getDate("Birthday").toLocalDate());
            return user;
        }
    }

    private boolean addFriendToDb(Long userId, Long friendId) {
        Integer statusId = getFriendStatusIdByName("Неподтверждённая");
        if (statusId != null) {
            String sql = "INSERT INTO \"UserFriend\" (\"UserID\", \"FriendID\", \"Status\") VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, userId, friendId, statusId);
            return true;
        } else {
            throw new IllegalStateException("Status 'Неподтверждённая' not found in FriendStatus table");
        }
    }

    @Override
    public Integer getFriendStatusIdByName(String statusName) {
        String sql = "SELECT \"ID\" FROM \"FriendStatus\" WHERE \"Name\" = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{statusName}, Integer.class);
    }

    @Override
    public Set<User> getAllFriends(Long userId) {
        // Получаем список друзей пользователя
        String sql = "SELECT u.* FROM \"User\" u " +
                "INNER JOIN \"UserFriend\" uf ON u.\"ID\" = uf.\"FriendID\" " +
                "WHERE uf.\"UserID\" = ?";
        List<User> friends = jdbcTemplate.query(sql, new Object[]{userId}, new UserRowMapper());

        return new HashSet<>(friends);
    }

    @Override
    public Set<User> getUserFriends(Long userId) {
        String sql = "SELECT u.* FROM \"User\" u INNER JOIN \"UserFriend\" uf ON u.\"ID\" = uf.\"FriendID\" WHERE uf.\"UserID\" = ?";
        return new HashSet<>(jdbcTemplate.query(sql, new Object[]{userId}, new UserRowMapper()));
    }

}