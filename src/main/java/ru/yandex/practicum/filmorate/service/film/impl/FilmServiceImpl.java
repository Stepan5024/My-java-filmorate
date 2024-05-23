package ru.yandex.practicum.filmorate.service.film.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage = new InMemoryFilmStorage();

    public Film addFilm(Film film) {
        Film newFilm = filmStorage.addFilm(film);
        log.info("Added new film with ID: {} and title: {}", newFilm.getId(), film.getName());
        return newFilm;
    }

    public Film updateFilm(Long id, Film film) {
        Film newFilm = filmStorage.updateFilm(id, film);

        if (newFilm == null) {
            log.warn("Attempted to update non-existent film with ID: {}", id);
            return null;
        } else {
            log.info("Updated film with ID: {}", id);
            return film;
        }
    }

    public List<Film> getAllFilms() {
        log.debug("Fetching all films.");
        return filmStorage.getAllFilms();
    }
}