package com.example.cinema.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDto {
    private long totalUsers;
    private long totalMovies;
    private long totalHalls;
}
