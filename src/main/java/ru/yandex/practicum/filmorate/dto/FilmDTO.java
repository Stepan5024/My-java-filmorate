package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.time.LocalDate;
import java.util.Set;

public class FilmDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> likes;
    private MPARating mpaRating;
    private Set<Long> genres;

    // геттеры и сеттеры
    // конструкторы
    // при необходимости другие методы
}