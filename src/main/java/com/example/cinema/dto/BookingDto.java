package com.example.cinema.dto;

import com.example.cinema.entity.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private Long scheduleId;
    private Long userId;
    private String seatNumber;
    private BookingStatus status;
    private LocalDateTime bookedAt;
}
