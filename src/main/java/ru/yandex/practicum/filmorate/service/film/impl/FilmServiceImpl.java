package ru.yandex.practicum.filmorate.service.film.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.service.film.FilmDependencyFacade;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmStorage;
    private final UserService userService;
    private final FilmDependencyFacade filmDependencyFacade;

    @Autowired
    public FilmServiceImpl(FilmDbStorage filmStorage, UserService userService,
                           FilmDependencyFacade filmDependencyFacade) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmDependencyFacade = filmDependencyFacade;
    }

    @Override
    public Film addFilm(Film film) {
        // Проверяем наличие mpa по заданному ID
        log.info("Try to validate MPARating");
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            filmDependencyFacade.validateAndGetMPARating(film.getMpa().getId());
        }
        log.info("Try to validate Genres");
        // Проверяем наличие жанров по заданным ID
        filmDependencyFacade.validateGenres(film.getGenres());
        log.info("Try to save into db");
        Film newFilm = filmStorage.addFilm(film);
        log.info("Added new film with ID: {} and title: {}", newFilm.getId(), film.getName());
        return newFilm;
    }

    @Override
    public Film updateFilm(Long id, Film film) {
        Film newFilm = filmStorage.updateFilm(id, film);

        if (newFilm == null) {
            return null;
        } else {
            return film;
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = userService.getUserById(userId);
        film.getLikes().add(user.getId());
        filmStorage.updateFilm(film.getId(), film);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = userService.getUserById(userId);

        film.getLikes().remove(user.getId());
        filmStorage.updateFilm(film.getId(), film);
    }

    @Override
    public List<Film> getTopFilms(int limit) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Film findById(Long id) {
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NoSuchElementException("Film not found with ID: " + id));

        // Проверка на пустоту или наличие id = 0 в поле Genres
        if (film.getGenres() == null || film.getGenres().isEmpty() || film.getGenres().stream().anyMatch(genre -> genre.getId() == 0)) {
            //film.setGenres(null);
        }

        return film;
    }

    @Override
    public List<Film> findAllFilmsWithDetails() {
        return filmStorage.findAllFilmsWithDetails();
    }

}