package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public User addUser(User user) {
        long id = currentId++;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public User updateUser(Long id, User user) {

        if (id == null || !users.containsKey(id)) {
            log.warn("Attempted to update non-existing user with ID: {}", id);
            return null;
        }
        user.setId(id);
        users.put(id, user);
        log.info("Updated user with ID: {} and details: {}", id, user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}