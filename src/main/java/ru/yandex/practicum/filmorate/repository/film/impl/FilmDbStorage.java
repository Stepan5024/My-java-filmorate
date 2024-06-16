package ru.yandex.practicum.filmorate.repository.film.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmDbStorage implements FilmRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*@Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO film (name, description, release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaRating().toString());
        return film;
    }

     */
/*
    @Override
    public Film addFilm(Film film) {
        log.info("add film into Films");
        String sql = "INSERT INTO \"Film\" (\"Name\", \"Description\", \"ReleaseDate\", \"Duration\", \"MPARatingID\") " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            assert film.getMpa() != null;
            ps.setInt(5, Math.toIntExact(film.getMpa().getId()));  // Assuming MPARating has an 'id' field
            return ps;
        }, keyHolder);

        // Retrieve the generated id
        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        // Set the generated id back to the film object
        film.setId(generatedId);
        log.info("add genres into GenreInFilm");
        // Insert genres into GenreInFilm table if genres collection is not null or empty
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreInsertSql = "INSERT INTO \"GenreInFilm\" (\"FilmID\", \"GenreID\") VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                log.info("id genre {}", genre.getId());
                //System.out.println( + " e");
                jdbcTemplate.update(genreInsertSql, generatedId, genre.getId());
            }
        }

        return film;
    }


 */

    @Override
    public Film addFilm(Film film) {
        // Insert film into Film table
        String filmSql = "INSERT INTO \"Film\" (\"Name\", \"Description\", \"ReleaseDate\", \"Duration\", \"MPARatingID\") " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.info("add Film to row in table");
        try {
            jdbcTemplate.update((PreparedStatementCreator) connection -> {
                PreparedStatement ps = connection.prepareStatement(filmSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setInt(4, film.getDuration());
                ps.setLong(5, film.getMpa().getId());  // Assuming MPARating has an 'id' field
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            log.error("Failed to insert film: {}", e.getMessage());
            throw new RuntimeException("Failed to insert film", e);
        }
        log.info("Film inserted successfully with ID: {}", Objects.requireNonNull(keyHolder.getKey()).longValue());
        // Retrieve the generated id
        Long generatedFilmId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        // Set the generated id back to the film object
        film.setId(generatedFilmId);
        log.info("add genres into GenreInFilm");
        // Insert genres into GenreInFilm table if genres collection is not null or empty
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO \"GenreInFilm\" (\"FilmID\", \"GenreID\") VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                log.info("id genre {}", genre.getId());
                jdbcTemplate.update(genreSql, generatedFilmId, genre.getId());
            }
        }
        log.info("All added genres");
        return film;
    }

    @Override
    public boolean deleteFilm(Long id) {
        String sql = "DELETE FROM \"Film\" WHERE id = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    /*@Override
    public Film updateFilm(Long id, Film film) {
        String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpaRating().toString(), id);
        return film;
    }*/
    @Override
    public Film updateFilm(Long id, Film film) {
        String sql = "UPDATE \"Film\" SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                id);
        return film;
    }

    public List<Film> findAllFilmsWithDetails() {
        String sql = "SELECT " +
                "f.\"ID\" AS film_id, " +
                "f.\"Name\" AS film_name, " +
                "f.\"Description\" AS film_description, " +
                "f.\"ReleaseDate\" AS film_release_date, " +
                "f.\"Duration\" AS film_duration, " +
                "mpa.\"ID\" AS mparating_id, " +
                "mpa.\"Name\" AS mparating_name, " +
                "mpa.\"Description\" AS mparating_description, " +
                "g.\"ID\" AS genre_id, " +
                "g.\"Name\" AS genre_name " +
                "FROM " +
                "\"Film\" f " +
                "LEFT JOIN \"MPARating\" mpa ON f.\"MPARatingID\" = mpa.\"ID\" " +
                "LEFT JOIN \"GenreInFilm\" gif ON f.\"ID\" = gif.\"FilmID\" " +
                "LEFT JOIN \"Genre\" g ON gif.\"GenreID\" = g.\"ID\"";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long filmId = rs.getLong("film_id");
            String filmName = rs.getString("film_name");
            String filmDescription = rs.getString("film_description");
            LocalDate filmReleaseDate = rs.getDate("film_release_date").toLocalDate();
            int filmDuration = rs.getInt("film_duration");

            Long mparatingId = rs.getLong("mparating_id");
            String mparatingName = rs.getString("mparating_name");
            String mparatingDescription = rs.getString("mparating_description");

            Long genreId = rs.getLong("genre_id");
            String genreName = rs.getString("genre_name");

            Film film = new Film(filmName, filmDescription, filmReleaseDate, filmDuration);
            film.setId(filmId);

            MPARating mpa = new MPARating();
            mpa.setId(mparatingId);
            mpa.setName(mparatingName);
            mpa.setDescription(mparatingDescription);

            film.setMpa(mpa);

            Set<Genre> genres = film.getGenres();
            if (genres == null) {
                genres = new HashSet<>();
                film.setGenres(genres);
            }

            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(genreName);
            genres.add(genre);

            return film;
        });
    }


    public Optional<Film> getFilmById(Long id) {
        String filmSql = "SELECT " +
                "f.\"ID\" AS film_id, " +
                "f.\"Name\" AS film_name, " +
                "f.\"Description\" AS film_description, " +
                "f.\"ReleaseDate\" AS film_release_date, " +
                "f.\"Duration\" AS film_duration, " +
                "mpa.\"ID\" AS mparating_id, " +
                "mpa.\"Name\" AS mparating_name, " +
                "mpa.\"Description\" AS mparating_description " +
                "FROM " +
                "\"Film\" f " +
                "LEFT JOIN \"MPARating\" mpa ON f.\"MPARatingID\" = mpa.\"ID\" " +
                "WHERE f.\"ID\" = ?";

        String genresSql = "SELECT " +
                "g.\"ID\" AS genre_id, " +
                "g.\"Name\" AS genre_name " +
                "FROM \"Genre\" g " +
                "INNER JOIN \"GenreInFilm\" gif ON g.\"ID\" = gif.\"GenreID\" " +
                "WHERE gif.\"FilmID\" = ? " +
                "ORDER BY g.\"ID\"";

        try {
            Film film = jdbcTemplate.queryForObject(filmSql, new Object[]{id}, (rs, rowNum) -> {
                String filmName = rs.getString("film_name");
                String filmDescription = rs.getString("film_description");
                LocalDate filmReleaseDate = rs.getDate("film_release_date").toLocalDate();
                int filmDuration = rs.getInt("film_duration");

                Long mparatingId = rs.getLong("mparating_id");
                String mparatingName = rs.getString("mparating_name");
                String mparatingDescription = rs.getString("mparating_description");

                Film mappedFilm = new Film(filmName, filmDescription, filmReleaseDate, filmDuration);
                mappedFilm.setId(id);

                MPARating mpa = new MPARating();
                mpa.setId(mparatingId);
                mpa.setName(mparatingName);
                mpa.setDescription(mparatingDescription);

                mappedFilm.setMpa(mpa);

                return mappedFilm;
            });

            // Получаем все жанры для фильма
            Set<Genre> genres = new HashSet<>(jdbcTemplate.query(genresSql, new Object[]{id}, (rs, rowNum) -> {
                Long genreId = rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");

                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(genreName);

                return genre;
            }));

            assert film != null;
            film.setGenres(genres);

            return Optional.of(film);
        } catch (Exception e) {
            log.error("Failed to get film by id: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setLikes(parseLikes(rs.getString("likes")));

            // Set MPARating if exists
            Long mpaId = rs.getLong("mpa_id");
            if (!rs.wasNull()) {
                String mpaName = rs.getString("mpa_name");
                String mpaDescription = rs.getString("mpa_description");
                MPARating mpaRating = new MPARating(mpaId, mpaName, mpaDescription);
                film.setMpa(mpaRating);
            }

            film.setGenres(new HashSet<>());

            // Set Genre if exists
            Long genreId = rs.getLong("genre_id");
            if (!rs.wasNull()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }

            return film;
        }
    }


    /* @Override
    public Optional<Film> getFilmById(Long id) {
        String sql = "SELECT * FROM \"Film\" WHERE id = ?";
        log.info("Execute getFilmById query with film id {}", id);
        Film film = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Film.class), id);
        log.info("Execute getFilmById query with film id {}", id);
        return Optional.ofNullable(film);
    }

    */
    private static Set<Long> parseLikes(String likes) {
        if (likes == null || likes.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(likes.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM \"Film\"";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Film.class));
    }
}