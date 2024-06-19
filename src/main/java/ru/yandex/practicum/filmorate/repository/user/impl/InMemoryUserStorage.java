package ru.yandex.practicum.filmorate.repository.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;


import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserRepository {
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

    @Override
    public Integer getFriendStatusIdByName(String statusName) {
        // Since it's in-memory, we can return a hardcoded value or create a map of statuses if needed.
        // Assuming "PENDING" status has ID 1 for this example.
        Map<String, Integer> statusMap = Map.of(
                "Неподтверждённая", 1,
                "Подтверждённая", 2
        );
        return statusMap.getOrDefault(statusName, 0);
    }

    @Override
    public Set<User> getAllFriends(Long userId) {
        return getUserById(userId)
                .map(User::getFriends)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<User> getUserFriends(Long userId) {
        return getAllFriends(userId);
    }
}