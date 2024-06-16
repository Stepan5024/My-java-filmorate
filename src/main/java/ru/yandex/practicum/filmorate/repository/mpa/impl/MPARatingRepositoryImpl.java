package ru.yandex.practicum.filmorate.repository.mpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.repository.mpa.IMPARatingRepository;

import java.util.List;
import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MPARatingRepositoryImpl implements IMPARatingRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPARatingRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class MPARatingRowMapper implements RowMapper<MPARating> {
        @Override
        public MPARating mapRow(ResultSet rs, int rowNum) throws SQLException {
            MPARating mpaRating = new MPARating();
            mpaRating.setId(rs.getLong("id"));
            mpaRating.setName(rs.getString("name"));
            mpaRating.setDescription(rs.getString("description"));
            return mpaRating;
        }
    }

    public Optional<MPARating> findById(Long id) {
        String sql = "SELECT * FROM \"MPARating\" WHERE id = ?";
        try {
            MPARating mpaRating = jdbcTemplate.queryForObject(sql, new MPARatingRowMapper(), id);
            log.info("Found MPARating with ID {}: {}", id, mpaRating);
            return Optional.ofNullable(mpaRating);
        } catch (Exception e) {
            log.error("Failed to find MPARating with ID {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<MPARating> getAllMPARatings() {
        log.debug("Fetching all mpa`s.");
        String sql = "SELECT * FROM \"MPARating\"";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MPARating.class));
    }
}
