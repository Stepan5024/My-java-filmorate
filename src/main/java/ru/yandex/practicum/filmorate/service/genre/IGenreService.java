package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface IGenreService {
    Genre findById(Long id);

    List<Genre> getAllGenres();
}