package ru.yandex.practicum.filmorate.repository.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;


import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmRepository {
    private final Map<Long, Film> films = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public Film addFilm(Film film) {
        long id = currentId++;
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public boolean deleteFilm(Long id) {
        if (films.containsKey(id)) {
            films.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public Film updateFilm(Long id, Film film) {

        if (id == null || !films.containsKey(id)) {
            log.warn("Attempted to update non-existent film with ID: {}", id);
            return null;
        }
        film.setId(id);
        films.put(id, film);
        log.info("Updated film with ID: {}", id);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Fetching all films.");
        return new ArrayList<>(films.values());
    }

}
