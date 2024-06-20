package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Sql({"/schema.sql", "/data.sql"})
class FilmoRateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userStorage.getAllUsers().forEach(user -> userStorage.deleteUser(user.getId()));

        User user = new User();
        user.setEmail("test" + UUID.randomUUID() + "@example.com");
        user.setLogin("testlogin" + UUID.randomUUID());
        user.setName("Test User" + UUID.randomUUID());
        user.setBirthday(LocalDate.of(1990, 1, 1));

        testUser = userStorage.addUser(user);
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(testUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", testUser.getId())
                );
    }
}
