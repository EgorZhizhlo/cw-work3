package com.example.cinema.repository;

import com.example.cinema.entity.Booking;
import com.example.cinema.entity.Schedule;
import com.example.cinema.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findBySchedule(Schedule schedule);
    Optional<Booking> findByScheduleAndSeatNumber(Schedule schedule, String seatNumber);
}
