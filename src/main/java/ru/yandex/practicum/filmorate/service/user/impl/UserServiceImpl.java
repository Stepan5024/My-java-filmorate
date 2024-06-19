package ru.yandex.practicum.filmorate.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;
import ru.yandex.practicum.filmorate.repository.user.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;

    @Autowired
    public UserServiceImpl(UserDbStorage userStorage) {
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

    @Override
    public Set<User> getAllFriends(Long id) {
        User user = getUserById(id, "Friend");

        // Преобразуем идентификаторы друзей в объекты User
        return userStorage.getUserFriends(user.getId());

    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        log.info("userId = {} and friendId = {}", userId, friendId);

        // Получаем пользователя и его друга
        User user = getUserById(userId, "User");
        User friend = getUserById(friendId, "Friend");

        // Добавляем друга в список друзей пользователя
        user.getFriends().add(friend);
        //friend.getFriends().add(user);

        // Обновляем пользователя и его друга в базе данных
        return userStorage.updateUser(user.getId(), user);
        //userStorage.updateUser(friend.getId(), friend);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId, "User");
        User friend = getUserById(friendId, "Friend");
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.updateUser(user.getId(), user);
        userStorage.updateUser(friend.getId(), friend);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = getUserById(userId, "User");
        User friend = getUserById(friendId, "Friend");
        Set<User> mutualFriends = new HashSet<>(user.getFriends());

        mutualFriends.retainAll(friend.getFriends());

        return new ArrayList<>(mutualFriends);
    }

    @Override
    public User getUserById(Long userId, String userType) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException(userType + " not found with ID: " + userId));
    }

    @Override
    public User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    }
}