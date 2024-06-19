package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    User addUser(User user);

    boolean deleteUser(Long id);

    User updateUser(Long id, User user);

    Optional<User> getUserById(Long id);

    List<User> getAllUsers();


    Integer getFriendStatusIdByName(String statusName);

    Set<User> getAllFriends(Long userId);

    Set<User> getUserFriends(Long userId);
}
