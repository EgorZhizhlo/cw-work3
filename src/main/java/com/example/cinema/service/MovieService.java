package com.example.cinema.service;

import com.example.cinema.dto.MovieDto;

import java.util.List;

public interface MovieService {
    /** Все фильмы (CLIENT + EMPLOYEE) */
    List<MovieDto> getAllMovies();

    /** Детали фильма по id (CLIENT + EMPLOYEE) */
    MovieDto getMovieById(Long id);

    /** Создать новый фильм (EMPLOYEE) */
    MovieDto createMovie(MovieDto movieDto);

    /** Обновить фильм (EMPLOYEE) */
    MovieDto updateMovie(Long id, MovieDto movieDto);

    /** Удалить фильм (EMPLOYEE) */
    void deleteMovie(Long id);
}
