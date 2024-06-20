package ru.yandex.practicum.filmorate.service.genre.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.IGenreRepository;
import ru.yandex.practicum.filmorate.service.genre.IGenreService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GenreServiceImpl implements IGenreService {

    private final IGenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(IGenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre findById(Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        return genre.orElse(null);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }
}