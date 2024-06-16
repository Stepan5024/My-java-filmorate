package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.genre.IGenreService;
import ru.yandex.practicum.filmorate.service.genre.impl.GenreServiceImpl;
import ru.yandex.practicum.filmorate.service.mpa.IMPARatingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final IGenreService genreService;

    @Autowired
    public GenreController(IGenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        // получить все жанры
        log.debug("Fetching all genres.");
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.status(HttpStatus.OK).body(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getGenreById(@PathVariable("id") Long id) {
        Genre genre = genreService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(genre);
    }
}