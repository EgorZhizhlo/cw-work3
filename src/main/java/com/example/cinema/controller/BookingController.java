package com.example.cinema.controller;

import com.example.cinema.dto.BookingDto;
import com.example.cinema.dto.request.BookingRequest;
import com.example.cinema.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /** Клиент создаёт бронь */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingRequest req) {
        BookingDto created = bookingService.createBooking(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /** Клиент смотрит свои бронирования */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    public List<BookingDto> getMyBookings() {
        return bookingService.getCurrentUserBookings();
    }

    /** Просмотр одной брони (CLIENT свою, EMPLOYEE любую) */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','EMPLOYEE')")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    /** Клиент или EMPLOYEE может отменить бронь */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','EMPLOYEE')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    /** EMPLOYEE: список всех броней */
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
