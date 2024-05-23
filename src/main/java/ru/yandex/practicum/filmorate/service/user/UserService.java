package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User createUser(User user);

    User updateUser(Long id, User user);

    List<User> getAllUsers();

    User getUserById(Long userId);

    Set<User> getAllFriends(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long friendId);
}
