package com.example.cinema.service;

import com.example.cinema.dto.ScheduleDto;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDto> getAllSchedules();
    ScheduleDto getScheduleById(Long id);
    ScheduleDto createSchedule(ScheduleDto scheduleDto);
    ScheduleDto updateSchedule(Long id, ScheduleDto scheduleDto);
    void deleteSchedule(Long id);
    List<ScheduleDto> getSchedulesByMovie(Long movieId);
    List<ScheduleDto> getSchedulesByHall(Long hallId);
}
