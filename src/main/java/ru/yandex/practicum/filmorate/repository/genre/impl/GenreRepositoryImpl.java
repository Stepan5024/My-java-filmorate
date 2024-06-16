package ru.yandex.practicum.filmorate.repository.genre.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.repository.genre.IGenreRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreRepositoryImpl implements IGenreRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }
    }

    @Override
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT * FROM \"Genre\" WHERE \"ID\" = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new GenreRowMapper(), id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("Fetching all genres.");
        String sql = "SELECT * FROM \"Genre\" ORDER BY \"ID\"";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class));
    }
}