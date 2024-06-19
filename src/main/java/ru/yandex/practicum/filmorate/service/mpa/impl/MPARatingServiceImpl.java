package ru.yandex.practicum.filmorate.service.mpa.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.repository.mpa.IMPARatingRepository;
import ru.yandex.practicum.filmorate.service.mpa.IMPARatingService;

import java.util.List;
import java.util.Optional;

@Service
public class MPARatingServiceImpl implements IMPARatingService {

    private final IMPARatingRepository mpaRatingRepository;

    @Autowired
    public MPARatingServiceImpl(IMPARatingRepository mpaRatingRepository) {
        this.mpaRatingRepository = mpaRatingRepository;
    }

    public MPARating findById(Long id) {
        Optional<MPARating> mpaRating = mpaRatingRepository.findById(id);
        return mpaRating.orElse(null);
    }

    @Override
    public List<MPARating> getAllMPARatings() {
        return mpaRatingRepository.getAllMPARatings();
    }
}