// File: src/main/java/com/example/cinema/controller/WebAdminScheduleController.java
package com.example.cinema.controller;

import com.example.cinema.dto.HallDto;
import com.example.cinema.dto.MovieDto;
import com.example.cinema.dto.ScheduleDto;
import com.example.cinema.service.HallService;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/schedules")
public class WebAdminScheduleController {

    private final ScheduleService scheduleService;
    private final MovieService movieService;
    private final HallService hallService;

    @Autowired
    public WebAdminScheduleController(ScheduleService scheduleService,
                                      MovieService movieService,
                                      HallService hallService) {
        this.scheduleService = scheduleService;
        this.movieService = movieService;
        this.hallService = hallService;
    }

    @ModelAttribute("movieService")
    public MovieService movieService() {
        return movieService;
    }

    @ModelAttribute("hallService")
    public HallService hallService() {
        return hallService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "admin/admin-schedules";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("schedule", new ScheduleDto());
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("halls", hallService.getAllHalls());
        return "admin/admin-schedule-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("schedule", scheduleService.getScheduleById(id));
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("halls", hallService.getAllHalls());
        return "admin/admin-schedule-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("schedule") ScheduleDto dto,
                       BindingResult br,
                       RedirectAttributes ra,
                       Model model) {
        if (br.hasErrors()) {
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("halls", hallService.getAllHalls());
            return "admin/admin-schedule-form";
        }
        if (dto.getId() == null) {
            scheduleService.createSchedule(dto);
            ra.addFlashAttribute("message", "Сеанс создан");
        } else {
            scheduleService.updateSchedule(dto.getId(), dto);
            ra.addFlashAttribute("message", "Сеанс обновлён");
        }
        return "redirect:/admin/schedules";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        scheduleService.deleteSchedule(id);
        ra.addFlashAttribute("message", "Сеанс удалён");
        return "redirect:/admin/schedules";
    }
}
