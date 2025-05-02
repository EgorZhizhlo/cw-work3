package com.example.cinema.controller;

import com.example.cinema.dto.HallDto;
import com.example.cinema.dto.MovieDto;
import com.example.cinema.dto.ScheduleDto;
import com.example.cinema.dto.request.BookingRequest;
import com.example.cinema.security.SecurityUtil;
import com.example.cinema.service.BookingService;
import com.example.cinema.service.HallService;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/movies")
public class WebMovieController {

    private final MovieService movieService;
    private final ScheduleService scheduleService;
    private final BookingService bookingService;
    private final HallService hallService;

    @Autowired
    public WebMovieController(MovieService movieService,
                              ScheduleService scheduleService,
                              BookingService bookingService,
                              HallService hallService) {
        this.movieService = movieService;
        this.scheduleService = scheduleService;
        this.bookingService = bookingService;
        this.hallService = hallService;
    }

    /** Список фильмов */
    @GetMapping
    public String list(Model model) {
        List<MovieDto> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        return "movies";
    }

    /** Детали фильма + сеансы */
    @GetMapping("/{movieId}")
    public String detail(@PathVariable Long movieId, Model model) {
        MovieDto movie = movieService.getMovieById(movieId);
        List<ScheduleDto> schedules = scheduleService.getSchedulesByMovie(movieId);
        model.addAttribute("movie", movie);
        model.addAttribute("schedules", schedules);
        return "movie-detail";
    }

    /** Форма выбора места */
    @GetMapping("/{movieId}/schedules/{scheduleId}/book")
    public String bookForm(@PathVariable Long movieId,
                           @PathVariable Long scheduleId,
                           Model model,
                           HttpServletRequest req) {
        // проверяем аутентификацию
        if (SecurityUtil.getCurrentUsername() == null) {
            // редиректим на логин, сохранив исходный URL
            String redirect = req.getRequestURI();
            return "redirect:/login?redirect=" + redirect;
        }

        ScheduleDto schedule = scheduleService.getScheduleById(scheduleId);
        HallDto hall = hallService.getHallById(schedule.getHallId());

        // список занятых мест
        List<String> booked = bookingService.getBookedSeats(scheduleId);

        // генерируем номера мест от 1 до capacity
        List<String> seatNumbers = IntStream.rangeClosed(1, hall.getCapacity())
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());

        model.addAttribute("schedule", schedule);
        model.addAttribute("hall", hall);
        model.addAttribute("bookedSeats", booked);
        model.addAttribute("seatNumbers", seatNumbers);
        return "booking";
    }

    /** Обработка отправки формы брони */
    @PostMapping("/{movieId}/schedules/{scheduleId}/book")
    public String bookSubmit(@PathVariable Long movieId,
                             @PathVariable Long scheduleId,
                             @RequestParam String seatNumber,
                             RedirectAttributes ra,
                             HttpServletRequest req) {
        if (SecurityUtil.getCurrentUsername() == null) {
            String redirect = req.getRequestURI();
            return "redirect:/login?redirect=" + redirect;
        }

        try {
            bookingService.createBooking(new BookingRequest(scheduleId, seatNumber));
            ra.addFlashAttribute("message", "Место " + seatNumber + " успешно забронировано");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movies/" + movieId;
    }
}
