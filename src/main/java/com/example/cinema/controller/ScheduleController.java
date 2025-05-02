// File: src/main/java/com/example/cinema/controller/ScheduleController.java
package com.example.cinema.controller;

import com.example.cinema.dto.ScheduleDto;
import com.example.cinema.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<ScheduleDto> list() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ScheduleDto get(@PathVariable Long id) {
        return scheduleService.getScheduleById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ScheduleDto> create(@Valid @RequestBody ScheduleDto dto) {
        ScheduleDto created = scheduleService.createSchedule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ScheduleDto update(@PathVariable Long id,
                              @Valid @RequestBody ScheduleDto dto) {
        return scheduleService.updateSchedule(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie/{movieId}")
    public List<ScheduleDto> byMovie(@PathVariable Long movieId) {
        return scheduleService.getSchedulesByMovie(movieId);
    }

    @GetMapping("/hall/{hallId}")
    public List<ScheduleDto> byHall(@PathVariable Long hallId) {
        return scheduleService.getSchedulesByHall(hallId);
    }
}
