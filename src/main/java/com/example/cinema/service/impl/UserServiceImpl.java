package com.example.cinema.service.impl;

import com.example.cinema.dto.BookingDto;
import com.example.cinema.dto.UserDto;
import com.example.cinema.entity.User;
import com.example.cinema.mapper.EntityDtoMapper;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.UserRepository;
import com.example.cinema.security.SecurityUtil;
import com.example.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public UserDto getCurrentUser() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User is not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return EntityDtoMapper.toDto(user);
    }

    @Override
    public UserDto updateProfile(UserDto userDto) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User is not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Обновляем поля, доступные для редактирования
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        User updated = userRepository.save(user);
        return EntityDtoMapper.toDto(updated);
    }

    @Override
    public List<BookingDto> getCurrentUserBookings() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User is not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return bookingRepository.findByUser(user).stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}