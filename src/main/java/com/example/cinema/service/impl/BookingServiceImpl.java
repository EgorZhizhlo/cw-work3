package com.example.cinema.service.impl;

import com.example.cinema.dto.BookingDto;
import com.example.cinema.dto.request.BookingRequest;
import com.example.cinema.entity.*;
import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.mapper.EntityDtoMapper;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.ScheduleRepository;
import com.example.cinema.repository.UserRepository;
import com.example.cinema.security.SecurityUtil;
import com.example.cinema.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ScheduleRepository scheduleRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto createBooking(BookingRequest request) {
        // Найти сеанс
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Schedule", "id", request.getScheduleId())
                );

        // Найти пользователя
        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Проверить, что место ещё не занято
        boolean exists = bookingRepository.findByScheduleAndSeatNumber(schedule, request.getSeatNumber())
                .isPresent();
        if (exists) {
            throw new IllegalArgumentException("Место " + request.getSeatNumber() + " уже забронировано");
        }

        Booking booking = Booking.builder()
                .schedule(schedule)
                .user(user)
                .seatNumber(request.getSeatNumber())
                .status(BookingStatus.ACTIVE)
                .build();

        Booking saved = bookingRepository.save(booking);
        return EntityDtoMapper.toDto(saved);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isOwner = booking.getUser().getId().equals(user.getId());
        boolean isEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.EMPLOYEE);

        if (!isOwner && !isEmployee) {
            throw new AccessDeniedException("Нет прав отменить эту бронь");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isOwner = booking.getUser().getId().equals(user.getId());
        boolean isEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.EMPLOYEE);

        if (!isOwner && !isEmployee) {
            throw new AccessDeniedException("Нет прав просмотреть эту бронь");
        }

        return EntityDtoMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getCurrentUserBookings() {
        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return bookingRepository.findByUser(user).stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getBookedSeats(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));
        return bookingRepository.findBySchedule(schedule).stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .map(Booking::getSeatNumber)
                .collect(Collectors.toList());
    }
}
