package com.example.cinema.controller;

import com.example.cinema.dto.BookingDto;
import com.example.cinema.dto.MovieDto;
import com.example.cinema.dto.ScheduleDto;
import com.example.cinema.dto.UserDto;
import com.example.cinema.dto.request.BookingRequest;
import com.example.cinema.service.BookingService;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.ScheduleService;
import com.example.cinema.service.UserService;
import com.example.cinema.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class WebProfileController {

    private final UserService userService;
    private final BookingService bookingService;
    private final ScheduleService scheduleService;
    private final MovieService movieService;

    @Autowired
    public WebProfileController(UserService userService,
                                BookingService bookingService,
                                ScheduleService scheduleService,
                                MovieService movieService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.scheduleService = scheduleService;
        this.movieService = movieService;
    }

    /** Просмотр и редактирование профиля + список своих броней */
    @GetMapping
    public String profile(Model model) {
        UserDto user = userService.getCurrentUser();
        model.addAttribute("user", user);

        // Собственные бронирования с деталями
        List<BookingView> bookings = bookingService.getCurrentUserBookings().stream()
                .map(b -> {
                    ScheduleDto sch = scheduleService.getScheduleById(b.getScheduleId());
                    MovieDto movie = movieService.getMovieById(sch.getMovieId());
                    return new BookingView(
                            b.getId(),
                            movie.getTitle(),
                            sch.getStartTime(),
                            b.getSeatNumber(),
                            b.getStatus().name()
                    );
                })
                .collect(Collectors.toList());
        model.addAttribute("bookings", bookings);

        // Проверяем, EMPLOYEE ли пользователь
        boolean isEmployee = user.getRoles().contains("EMPLOYEE");
        model.addAttribute("isEmployee", isEmployee);

        return "profile";
    }

    /** Сохранение изменений профиля */
    @PostMapping
    public String updateProfile(@ModelAttribute UserDto userDto,
                                RedirectAttributes ra) {
        try {
            UserDto updated = userService.updateProfile(userDto);
            ra.addFlashAttribute("message", "Профиль обновлён");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    /** Отмена собственной брони */
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                RedirectAttributes ra) {
        try {
            bookingService.cancelBooking(id);
            ra.addFlashAttribute("message", "Бронь отменена");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    /** Вспомогательный DTO для отображения бронирований */
    public static record BookingView(
            Long bookingId,
            String movieTitle,
            java.time.LocalDateTime startTime,
            String seatNumber,
            String status
    ) {}
}
