package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.mpa.IMPARatingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MPARatingController {

    private final IMPARatingService mpaRatingService;

    @Autowired
    public MPARatingController(IMPARatingService mpaRatingService) {
        this.mpaRatingService = mpaRatingService;
    }

    @GetMapping
    public ResponseEntity<List<MPARating>> getAllMPA() {
        // получить все рейтинги
        log.debug("Fetching all mpa`s.");
        List<MPARating> mpaRatings = mpaRatingService.getAllMPARatings();
        return ResponseEntity.status(HttpStatus.OK).body(mpaRatings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMPARatingById(@PathVariable("id") Long id) {
        MPARating mpaRating = mpaRatingService.findById(id);
        if (mpaRating != null) {
            return ResponseEntity.status(HttpStatus.OK).body(mpaRating);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MPARating с указанным ID не найден");
        }
    }
}