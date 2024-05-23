package ru.yandex.practicum.filmorate.service.film.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        Film newFilm = filmStorage.addFilm(film);
        log.info("Added new film with ID: {} and title: {}", newFilm.getId(), film.getName());
        return newFilm;
    }

    public Film updateFilm(Long id, Film film) {
        Film newFilm = filmStorage.updateFilm(id, film);

        if (newFilm == null) {
            return null;
        } else {
            return film;
        }
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        film.getLikes().add(userId);
        filmStorage.updateFilm(film.getId(), film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> new IllegalArgumentException("Film not found"));
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film.getId(), film);
    }

    public List<Film> getTopFilms(int limit) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}