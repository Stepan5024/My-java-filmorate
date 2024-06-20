package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;
import java.util.Optional;

public interface IMPARatingRepository {
    Optional<MPARating> findById(Long id);

    List<MPARating> getAllMPARatings();
}