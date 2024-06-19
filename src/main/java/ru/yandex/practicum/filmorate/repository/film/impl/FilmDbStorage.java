package ru.yandex.practicum.filmorate.repository.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage implements FilmRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO \"UserFilmLike\" (\"UserID\", \"FilmID\") VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public Film addFilm(Film film) {
        // Insert film into Film table
        String filmSql = "INSERT INTO \"Film\" (\"Name\", \"Description\"," +
                " \"ReleaseDate\", \"Duration\", \"MPARatingID\") " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.info("add Film to row in table");
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(filmSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setObject(3, film.getReleaseDate());
                ps.setInt(4, film.getDuration());
                ps.setLong(5, film.getMpa().getId());
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            log.error("Failed to insert film: {}", e.getMessage());
            throw new RuntimeException("Failed to insert film", e);
        }
        log.info("Film inserted successfully with ID: {}", Objects.requireNonNull(keyHolder.getKey()).longValue());
        Long generatedFilmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(generatedFilmId);

        log.info("add genres into GenreInFilm");
        // вставка жанров к фильму
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO \"GenreInFilm\" (\"FilmID\", \"GenreID\") VALUES (?, ?)";
            log.info("Start save genres by film {}", film.getId());
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

    @Override
    public Film updateFilm(Long id, Film film) {
        String checkSql = "SELECT COUNT(*) FROM \"Film\" WHERE \"ID\" = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, new Object[]{id}, Integer.class);

        if (count == null || count == 0) {
            return null;
        }

        String sql = "UPDATE \"Film\" SET \"Name\" = ?, \"Description\" = ?, " +
                "\"ReleaseDate\" = ?, \"Duration\" = ? WHERE \"ID\" = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                id);

        // Обновляем лайки
        String deleteLikesSql = "DELETE FROM \"UserFilmLike\" WHERE \"FilmID\" = ?";
        jdbcTemplate.update(deleteLikesSql, id);

        String insertLikeSql = "INSERT INTO \"UserFilmLike\" (\"UserID\", \"FilmID\") VALUES (?, ?)";
        for (User user : film.getLikes()) {
            jdbcTemplate.update(insertLikeSql, user.getId(), id);
        }

        return film;
    }

    @Override
    public List<Film> findAllFilmsWithDetails() {
        String sql = "SELECT \"ID\" FROM \"Film\" ORDER BY \"ID\" ASC";
        List<Long> filmIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("ID"));
        List<Film> films = new ArrayList<>();
        for (Long filmId : filmIds) {
            getFilmById(filmId).ifPresent(films::add);
        }
        return films;
    }

    @Override
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

        String likesSql = "SELECT u.\"ID\", u.\"Email\", u.\"Login\", u.\"Name\", u.\"Birthday\" " +
                "FROM \"User\" u " +
                "INNER JOIN \"UserFilmLike\" ufl ON u.\"ID\" = ufl.\"UserID\" " +
                "WHERE ufl.\"FilmID\" = ?";

        try {
            Film film = jdbcTemplate.queryForObject(filmSql, new Object[]{id}, (rs, rowNum) -> {
                String filmName = rs.getString("film_name");
                String filmDescription = rs.getString("film_description");
                LocalDate filmReleaseDate = rs.getDate("film_release_date").toLocalDate();
                int filmDuration = rs.getInt("film_duration");

                Long mpaRatingId = rs.getLong("mparating_id");
                String mpaRatingName = rs.getString("mparating_name");
                String mpaRatingDescription = rs.getString("mparating_description");

                Film mappedFilm = new Film(filmName, filmDescription, filmReleaseDate, filmDuration);
                mappedFilm.setId(id);

                MPARating mpa = new MPARating();
                mpa.setId(mpaRatingId);
                mpa.setName(mpaRatingName);
                mpa.setDescription(mpaRatingDescription);

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

            // Получаем всех пользователей, которые лайкнули фильм
            Set<User> likes = new HashSet<>(jdbcTemplate.query(likesSql, new Object[]{id}, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getLong("ID"));
                user.setEmail(rs.getString("Email"));
                user.setLogin(rs.getString("Login"));
                user.setName(rs.getString("Name"));
                user.setBirthday(rs.getDate("Birthday").toLocalDate());
                return user;
            }));

            assert film != null;
            film.setGenres(genres);
            film.setLikes(likes);

            return Optional.of(film);
        } catch (Exception e) {
            log.error("Failed to get film by id: {}", e.getMessage());
            return Optional.empty();
        }
    }
}