package ru.yandex.practicum.filmorate.service.user.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
            return null;
        } else {
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow();
        User friend = userStorage.getUserById(friendId).orElseThrow();
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.updateUser(user.getId(), user);
        userStorage.updateUser(friend.getId(), friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow();
        User friend = userStorage.getUserById(friendId).orElseThrow();
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.updateUser(user.getId(), user);
        userStorage.updateUser(friend.getId(), friend);
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow();
        User friend = userStorage.getUserById(friendId).orElseThrow();
        Set<Long> mutualFriendIds = new HashSet<>(user.getFriends());

        mutualFriendIds.retainAll(friend.getFriends());
        return mutualFriendIds.stream()
                .map(id -> userStorage.getUserById(id).orElse(null))
                .collect(Collectors.toList());
    }
}