package ru.yandex.practicum.filmorate.service.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mpa.IMPARatingService;
import ru.yandex.practicum.filmorate.service.genre.IGenreService;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class FilmDependencyFacade {

    private final IMPARatingService mpaRatingService;
    private final IGenreService genreService;

    @Autowired
    public FilmDependencyFacade(IMPARatingService mpaRatingService, IGenreService genreService) {
        this.mpaRatingService = mpaRatingService;
        this.genreService = genreService;
    }

    public MPARating validateAndGetMPARating(Long mpaId) {
        Optional<MPARating> mpaRating = Optional.ofNullable(mpaRatingService.findById(mpaId));
        if (mpaRating.isEmpty()) {
            log.warn("MPARating с указанным ID {} не найден", mpaId);
            throw new IllegalArgumentException(String.format("MPARating с указанным ID %d не найден", mpaId));
        }
        return mpaRating.get();
    }


    public void validateGenres(Set<Genre> genres) {
        for (Genre genre : genres) {
            Genre foundGenre = genreService.findById(genre.getId());

            if (foundGenre == null) {
                log.warn("Genre с указанным ID {}} не найден", genre.getId());
                throw new IllegalArgumentException(String.format("Genre с указанным ID %d не найден", genre.getId()));
            }
            log.info("Genre Name {}", foundGenre.getName());
        }
    }
}