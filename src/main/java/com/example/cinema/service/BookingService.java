// File: src/main/java/com/example/cinema/service/BookingService.java
package com.example.cinema.service;

import com.example.cinema.dto.BookingDto;
import com.example.cinema.dto.request.BookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingRequest request);
    void cancelBooking(Long bookingId);
    BookingDto getBookingById(Long bookingId);
    List<BookingDto> getCurrentUserBookings();
    List<BookingDto> getAllBookings();

    /** Новый метод: список занятых мест для сеанса */
    List<String> getBookedSeats(Long scheduleId);
}
