package com.example.cinema.service.impl;

import com.example.cinema.dto.StatisticsDto;
import com.example.cinema.repository.HallRepository;
import com.example.cinema.repository.MovieRepository;
import com.example.cinema.repository.UserRepository;
import com.example.cinema.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    @Autowired
    public StatisticsServiceImpl(UserRepository userRepository,
                                 MovieRepository movieRepository,
                                 HallRepository hallRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public StatisticsDto getStatistics() {
        return StatisticsDto.builder()
                .totalUsers(userRepository.count())
                .totalMovies(movieRepository.count())
                .totalHalls(hallRepository.count())
                .build();
    }
}
