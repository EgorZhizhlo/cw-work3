package com.example.cinema.service;

import com.example.cinema.dto.HallDto;

import java.util.List;

public interface HallService {
    List<HallDto> getAllHalls();
    HallDto getHallById(Long id);
    HallDto createHall(HallDto hallDto);
    HallDto updateHall(Long id, HallDto hallDto);
    void deleteHall(Long id);
}
