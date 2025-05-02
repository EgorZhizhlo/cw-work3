// File: src/main/java/com/example/cinema/controller/AuthController.java
package com.example.cinema.controller;

import com.example.cinema.dto.request.LoginRequest;
import com.example.cinema.dto.request.SignupRequest;
import com.example.cinema.dto.response.JwtResponse;
import com.example.cinema.dto.UserDto;
import com.example.cinema.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private static final String COOKIE_NAME = "JWT";

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Вход: возвращаем payload + ставим HttpOnly cookie */
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(
            @Valid @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {
        JwtResponse jwtResp = authService.login(req);

        // создаём cookie с токеном
        Cookie cookie = new Cookie(COOKIE_NAME, jwtResp.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)(24*60*60)); // 1 день
        response.addCookie(cookie);

        // возвращаем body без token (он в cookie)
        jwtResp.setToken(null);
        return ResponseEntity.ok(jwtResp);
    }

    /** Регистрация: возвращаем UserDto */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody SignupRequest req
    ) {
        UserDto userDto = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    /** Выход: стереть куку */
    @PostMapping("/signout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }
}
