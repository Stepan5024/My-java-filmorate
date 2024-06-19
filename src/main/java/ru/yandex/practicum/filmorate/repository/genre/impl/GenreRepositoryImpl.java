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
import ru.yandex.practicum.filmorate.repository.mpa.impl.MPARatingRepositoryImpl;

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
            List<Genre> results = jdbcTemplate.query(sql, new GenreRowMapper(), id);
            if (results.isEmpty()) {
                log.info("genre with ID {} not found", id);
                return Optional.empty();
            } else {
                Genre genre = results.getFirst();
                log.info("Found genre with ID {}: {}", id, genre);
                return Optional.of(genre);
            }

        } catch (Exception e) {
            log.error("Failed to find genre with ID {}", id, e);
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