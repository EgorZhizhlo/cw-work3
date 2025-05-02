// File: src/main/java/com/example/cinema/controller/WebAdminMovieController.java
package com.example.cinema.controller;

import com.example.cinema.dto.MovieDto;
import com.example.cinema.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/movies")
public class WebAdminMovieController {

    private final MovieService movieService;

    @Autowired
    public WebAdminMovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /** Список всех фильмов */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "admin/admin-movies";
    }

    /** Форма создания нового фильма */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("movie", new MovieDto());
        return "admin/admin-movie-form";
    }

    /** Форма редактирования */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("movie", movieService.getMovieById(id));
        return "admin/admin-movie-form";
    }

    /** Обработка сохранения (создать или обновить) */
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("movie") MovieDto movieDto,
                       BindingResult br,
                       RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "admin/admin-movie-form";
        }
        if (movieDto.getId() == null) {
            movieService.createMovie(movieDto);
            ra.addFlashAttribute("message", "Фильм создан");
        } else {
            movieService.updateMovie(movieDto.getId(), movieDto);
            ra.addFlashAttribute("message", "Фильм обновлён");
        }
        return "redirect:/admin/movies";
    }

    /** Удаление */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        movieService.deleteMovie(id);
        ra.addFlashAttribute("message", "Фильм удалён");
        return "redirect:/admin/movies";
    }
}
