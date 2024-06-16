package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    Film addFilm(Film film);

    boolean deleteFilm(Long id);

    Film updateFilm(Long id, Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getAllFilms();

    List<Film> findAllFilmsWithDetails();
}
