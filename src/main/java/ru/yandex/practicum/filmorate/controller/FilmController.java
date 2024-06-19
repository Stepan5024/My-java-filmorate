package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final String LIKE_PATH = "/{id}/like/{userId}";
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
        log.info("FilmController initialized with FilmService.");
    }

    @PostMapping
    public ResponseEntity<Object> addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {

        // создать новый фильм
        log.info("Attempting to add a new film with title: {}", film.getName());

        for (Genre genre : film.getGenres()) {
            log.info("genre film: {}", genre.getId());
        }
        if (bindingResult.hasErrors()) {
            log.warn("Validation addFilm errors occurred: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Validation error: " + bindingResult.getAllErrors()));
        }

        validateReleaseDate(film.getReleaseDate());
        log.info("Try to put in service: {}", film.getName());
        Film savedFilm = filmService.addFilm(film);
        log.info("Film added successfully with ID: {}", savedFilm.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFilm);
    }

    @PutMapping
    public ResponseEntity<Object> updateFilm(@Valid @RequestBody Film film,
                                             BindingResult bindingResult) {
        // обновить фильм по Film
        if (bindingResult.hasErrors()) {
            log.warn("Validation updateFilm errors occurred: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Validation error: "
                    + bindingResult.getAllErrors()));
        }

        validateReleaseDate(film.getReleaseDate());

        Long filmId = film.getId();
        Film updatedFilm = filmService.updateFilm(filmId, film);
        if (updatedFilm == null) {
            log.warn("Failed to find film with ID: {} for update", filmId);
            throw new NoSuchElementException("Failed to find film with ID: " + filmId);
        }
        log.info("Film updated successfully with ID: {}", updatedFilm.getId());
        return ResponseEntity.status(HttpStatus.OK).body(updatedFilm);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateFilm(@PathVariable Long id, @Valid @RequestBody Film film,
                                             BindingResult bindingResult) {
        // обновить фильм по id
        log.info("Attempting to update film with ID: {}", id);
        if (bindingResult.hasErrors()) {
            log.warn("Validation updateFilm errors occurred: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Validation error: "
                    + bindingResult.getAllErrors()));
        }
        validateReleaseDate(film.getReleaseDate());
        Film updatedFilm = filmService.updateFilm(id, film);
        if (updatedFilm == null) {
            log.warn("Failed to find film with ID: {} for update", id);
            throw new NoSuchElementException("Film not found with ID: " + id);
        }
        log.info("Film updated successfully with ID: {}", updatedFilm.getId());
        return ResponseEntity.status(HttpStatus.OK).body(updatedFilm);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        LocalDate earliestReleaseFilmDate = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(earliestReleaseFilmDate)) {
            log.error("Release date validation failed for date: {}. It must be on or after 28th December 1895.",
                    releaseDate);
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFilmById(@PathVariable("id") Long id) {
        log.info("Get Film with ID: {}", id);
        Film film = filmService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllFilms() {
        log.info("Get All Films");
        List<Film> films = filmService.findAllFilmsWithDetails();
        return ResponseEntity.status(HttpStatus.OK).body(films);
    }


    @PutMapping(LIKE_PATH)
    public ResponseEntity<Film> addLike(@PathVariable Long id, @PathVariable Long userId) throws Exception {
        // пользователь ставит лайк фильму
        try {
            Film film = filmService.addLike(id, userId);
            return ResponseEntity.ok(film);
        } catch (NoSuchElementException e) {
            log.error("Failed to add like: {}", e.getMessage());
            throw new NoSuchElementException("Failed to add like: " + e.getMessage(), e);
        }
    }

    @DeleteMapping(LIKE_PATH)
    public ResponseEntity<Void> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        // пользователь удаляет лайк
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            log.error("Failed to remove like: {}", e.getMessage());
            throw new NoSuchElementException("Failed to remove like: " + e.getMessage(), e);
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        // возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано,
        // верните первые 10
        try {
            List<Film> films = filmService.getTopFilms(count);
            return ResponseEntity.ok(films);
        } catch (Exception e) {
            log.error("Error fetching top films: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
