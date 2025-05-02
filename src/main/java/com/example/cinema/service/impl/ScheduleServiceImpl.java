package com.example.cinema.service.impl;

import com.example.cinema.dto.ScheduleDto;
import com.example.cinema.entity.Hall;
import com.example.cinema.entity.Movie;
import com.example.cinema.entity.Schedule;
import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.mapper.EntityDtoMapper;
import com.example.cinema.repository.HallRepository;
import com.example.cinema.repository.MovieRepository;
import com.example.cinema.repository.ScheduleRepository;
import com.example.cinema.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    @Autowired
    public ScheduleServiceImpl(
            ScheduleRepository scheduleRepository,
            MovieRepository movieRepository,
            HallRepository hallRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.movieRepository = movieRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public List<ScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDto getScheduleById(Long id) {
        Schedule sch = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        return EntityDtoMapper.toDto(sch);
    }

    @Override
    public ScheduleDto createSchedule(ScheduleDto dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", dto.getMovieId()));
        Hall hall = hallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new ResourceNotFoundException("Hall", "id", dto.getHallId()));

        // Проверка пересечений
        var conflicts = scheduleRepository
                .findByHallAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        hall, dto.getEndTime(), dto.getStartTime());
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("В указанном зале в это время уже есть сеанс");
        }

        Schedule sch = Schedule.builder()
                .movie(movie)
                .hall(hall)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
        Schedule saved = scheduleRepository.save(sch);
        return EntityDtoMapper.toDto(saved);
    }

    @Override
    public ScheduleDto updateSchedule(Long id, ScheduleDto dto) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", dto.getMovieId()));
        Hall hall = hallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new ResourceNotFoundException("Hall", "id", dto.getHallId()));

        // Проверка пересечений (исключая сам этот сеанс)
        var conflicts = scheduleRepository
                .findByHallAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        hall, dto.getEndTime(), dto.getStartTime())
                .stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("В указанном зале в это время уже есть сеанс");
        }

        existing.setMovie(movie);
        existing.setHall(hall);
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());

        Schedule updated = scheduleRepository.save(existing);
        return EntityDtoMapper.toDto(updated);
    }

    @Override
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule", "id", id);
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    public List<ScheduleDto> getSchedulesByMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));
        return scheduleRepository.findByMovie(movie).stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto> getSchedulesByHall(Long hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new ResourceNotFoundException("Hall", "id", hallId));
        return scheduleRepository.findByHall(hall).stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
