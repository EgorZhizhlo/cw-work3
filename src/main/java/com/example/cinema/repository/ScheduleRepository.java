package com.example.cinema.repository;

import com.example.cinema.entity.Hall;
import com.example.cinema.entity.Schedule;
import com.example.cinema.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByHall(Hall hall);
    List<Schedule> findByMovie(Movie movie);

    // Найти пересекающиеся сеансы в зале
    List<Schedule> findByHallAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Hall hall,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}
