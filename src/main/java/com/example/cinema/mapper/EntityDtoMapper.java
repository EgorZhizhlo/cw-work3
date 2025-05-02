package com.example.cinema.mapper;

import com.example.cinema.dto.*;
import com.example.cinema.dto.request.SignupRequest;
import com.example.cinema.dto.response.JwtResponse;
import com.example.cinema.entity.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Collectors;

public class EntityDtoMapper {

    public static RoleDto toDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles()
                        .stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    public static MovieDto toDto(Movie m) {
        return MovieDto.builder()
                .id(m.getId())
                .title(m.getTitle())
                .description(m.getDescription())
                .durationMinutes(m.getDurationMinutes())
                .releaseDate(m.getReleaseDate())
                .posterUrl(m.getPosterUrl())
                .build();
    }

    public static HallDto toDto(Hall h) {
        return HallDto.builder()
                .id(h.getId())
                .name(h.getName())
                .capacity(h.getCapacity())
                .build();
    }

    public static ScheduleDto toDto(Schedule s) {
        return ScheduleDto.builder()
                .id(s.getId())
                .movieId(s.getMovie().getId())
                .hallId(s.getHall().getId())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .build();
    }

    public static BookingDto toDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .scheduleId(b.getSchedule().getId())
                .userId(b.getUser().getId())
                .seatNumber(b.getSeatNumber())
                .status(b.getStatus())
                .bookedAt(b.getBookedAt())
                .build();
    }

    // Для регистрации нового User из SignupRequest
    public static User toEntity(SignupRequest req, String encodedPassword) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encodedPassword);
        u.setEmail(req.getEmail());
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        return u;
    }

    // Для формирования JwtResponse из UserDetails + JWT + roles
    public static JwtResponse toJwtResponse(String token,
                                            UserDetails userDetails,
                                            Long id,
                                            String email,
                                            java.util.List<String> roles) {
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(id)
                .username(userDetails.getUsername())
                .email(email)
                .roles(roles)
                .build();
    }
}
