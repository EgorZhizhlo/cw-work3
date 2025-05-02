package com.example.cinema.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallDto {
    private Long id;
    private String name;
    private Integer capacity;
}
