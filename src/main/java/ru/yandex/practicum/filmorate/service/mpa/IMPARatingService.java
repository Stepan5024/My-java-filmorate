package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

public interface IMPARatingService {
    MPARating findById(Long id);

    List<MPARating> getAllMPARatings();
}