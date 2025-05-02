package com.example.cinema.service.impl;

import com.example.cinema.dto.HallDto;
import com.example.cinema.entity.Hall;
import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.mapper.EntityDtoMapper;
import com.example.cinema.repository.HallRepository;
import com.example.cinema.service.HallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;

    @Autowired
    public HallServiceImpl(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @Override
    public List<HallDto> getAllHalls() {
        return hallRepository.findAll().stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HallDto getHallById(Long id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hall", "id", id));
        return EntityDtoMapper.toDto(hall);
    }

    @Override
    public HallDto createHall(HallDto dto) {
        Hall hall = Hall.builder()
                .name(dto.getName())
                .capacity(dto.getCapacity())
                .build();
        Hall saved = hallRepository.save(hall);
        return EntityDtoMapper.toDto(saved);
    }

    @Override
    public HallDto updateHall(Long id, HallDto dto) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hall", "id", id));
        hall.setName(dto.getName());
        hall.setCapacity(dto.getCapacity());
        Hall updated = hallRepository.save(hall);
        return EntityDtoMapper.toDto(updated);
    }

    @Override
    public void deleteHall(Long id) {
        if (!hallRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hall", "id", id);
        }
        hallRepository.deleteById(id);
    }
}
