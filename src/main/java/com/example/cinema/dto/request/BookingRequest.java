package com.example.cinema.dto.request;

import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull
    private Long scheduleId;
    @NotBlank
    private String seatNumber;
}