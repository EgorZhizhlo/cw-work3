package com.example.cinema.service.impl;

import com.example.cinema.dto.UserDto;
import com.example.cinema.dto.request.LoginRequest;
import com.example.cinema.dto.request.SignupRequest;
import com.example.cinema.dto.response.JwtResponse;
import com.example.cinema.entity.Role;
import com.example.cinema.entity.RoleName;
import com.example.cinema.entity.User;
import com.example.cinema.mapper.EntityDtoMapper;
import com.example.cinema.repository.RoleRepository;
import com.example.cinema.repository.UserRepository;
import com.example.cinema.security.jwt.JwtUtils;
import com.example.cinema.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        // аутентификация Spring Security
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // генерация JWT
        String jwt = jwtUtils.generateJwtToken(auth);

        // получение полного User из БД
        User u = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + loginRequest.getUsername()));

        // роли для ответа
        List<String> roles = u.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        return EntityDtoMapper.toJwtResponse(jwt, userDetails, u.getId(), u.getEmail(), roles);
    }

    @Override
    public UserDto register(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // создание сущности User
        User newUser = EntityDtoMapper.toEntity(
                signupRequest,
                passwordEncoder.encode(signupRequest.getPassword())
        );

        // присваиваем роль CLIENT
        Role clientRole = roleRepository.findByName(RoleName.CLIENT)
                .orElseThrow(() -> new RuntimeException("Error: Role CLIENT not found."));
        newUser.setRoles(Collections.singleton(clientRole));

        User saved = userRepository.save(newUser);
        return EntityDtoMapper.toDto(saved);
    }
}
