package com.example.cinema.service;

import com.example.cinema.dto.UserDto;
import com.example.cinema.dto.request.LoginRequest;
import com.example.cinema.dto.request.SignupRequest;
import com.example.cinema.dto.response.JwtResponse;

public interface AuthService {
    /** Аутентификация — возвращает JWT и данные пользователя */
    JwtResponse login(LoginRequest loginRequest);

    /** Регистрация нового клиента — возвращает DTO созданного User */
    UserDto register(SignupRequest signupRequest);
}
