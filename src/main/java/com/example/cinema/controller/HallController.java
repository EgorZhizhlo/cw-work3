// File: src/main/java/com/example/cinema/controller/HallController.java
package com.example.cinema.controller;

import com.example.cinema.dto.HallDto;
import com.example.cinema.service.HallService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    private final HallService hallService;

    @Autowired
    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public List<HallDto> list() {
        return hallService.getAllHalls();
    }

    @GetMapping("/{id}")
    public HallDto get(@PathVariable Long id) {
        return hallService.getHallById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<HallDto> create(@Valid @RequestBody HallDto dto) {
        HallDto created = hallService.createHall(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public HallDto update(@PathVariable Long id,
                          @Valid @RequestBody HallDto dto) {
        return hallService.updateHall(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }
}
