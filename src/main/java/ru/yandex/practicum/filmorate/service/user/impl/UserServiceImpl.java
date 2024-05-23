package ru.yandex.practicum.filmorate.service.user.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.InMemoryUserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage = new InMemoryUserStorage();

    @Override
    public User createUser(User user) {

        User newUser = userStorage.addUser(user);
        log.info("Created new user with ID: {} and details: {}", newUser.getId(), user);
        return newUser;
    }

    @Override
    public User updateUser(Long id, User user) {

        User newUser = userStorage.updateUser(id, user);

        if (newUser == null) {
            log.warn("Attempted to update non-existing user with ID: {}", id);
            return null;
        } else {
            log.info("Updated user with ID: {} and details: {}", id, user);
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }
}