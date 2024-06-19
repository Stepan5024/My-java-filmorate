package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface IGenreService {
    Genre findById(Long id);

    List<Genre> getAllGenres();
}