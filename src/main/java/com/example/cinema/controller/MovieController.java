// File: src/main/java/com/example/cinema/controller/MovieController.java
package com.example.cinema.controller;

import com.example.cinema.dto.MovieDto;
import com.example.cinema.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDto> list() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public MovieDto get(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<MovieDto> create(@Valid @RequestBody MovieDto dto) {
        MovieDto created = movieService.createMovie(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public MovieDto update(@PathVariable Long id,
                           @Valid @RequestBody MovieDto dto) {
        return movieService.updateMovie(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
