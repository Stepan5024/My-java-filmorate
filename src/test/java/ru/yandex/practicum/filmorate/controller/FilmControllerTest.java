package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;
    private Film validFilm;
    private BindingResult bindingResult;

    @BeforeEach
    public void setup() {
        openMocks(this);
        validFilm = new Film();
        validFilm.setName("Inception");
        validFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        bindingResult = new BeanPropertyBindingResult(validFilm, "film");
    }


    @Test
    public void whenUpdateFilmWithBindingErrors_thenBadRequest() {
        bindingResult.rejectValue("name", "Error", "Name cannot be empty");

        ResponseEntity<Object> response = filmController.updateFilm(1L, validFilm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error")
                .toString().contains("Validation error"));
    }

    @Test
    public void whenAddFilmWithBindingErrors_thenBadRequest() {
        bindingResult.rejectValue("name", "Error", "Name cannot be empty");

        ResponseEntity<Object> response = filmController.addFilm(validFilm, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error")
                .toString().contains("Validation error"));
    }

}
