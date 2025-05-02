package com.example.cinema.service;

import com.example.cinema.dto.BookingDto;
import com.example.cinema.dto.UserDto;

import java.util.List;

public interface UserService {
    /** Профиль текущего пользователя */
    UserDto getCurrentUser();

    /** Обновление профиля (имя, фамилия, email) */
    UserDto updateProfile(UserDto userDto);

    /** Список собственных бронирований */
    List<BookingDto> getCurrentUserBookings();
}
