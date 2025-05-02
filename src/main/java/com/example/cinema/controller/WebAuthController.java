// File: src/main/java/com/example/cinema/controller/WebAuthController.java
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebAuthController {

    private final AuthService authService;
    private static final String COOKIE_NAME = "JWT";

    @Autowired
    public WebAuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Показать страницу логина */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }

    /** Обработать форму логина */
    @PostMapping("/login")
    public String loginProcess(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpServletResponse response,
            RedirectAttributes ra
    ) {
        try {
            JwtResponse jwtResp = authService.login(new LoginRequest(username, password));
            Cookie cookie = new Cookie(COOKIE_NAME, jwtResp.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) (24*60*60));
            response.addCookie(cookie);
            // куда редиректим после логина
            return (redirect != null && !redirect.isBlank()) ? "redirect:" + redirect : "redirect:/profile";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/login" + (redirect!=null? "?redirect="+redirect : "");
        }
    }

    /** Показать страницу регистрации */
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    /** Обработать форму регистрации */
    @PostMapping("/signup")
    public String signupProcess(
            @ModelAttribute @Valid SignupRequest signupRequest,
            RedirectAttributes ra
    ) {
        try {
            UserDto user = authService.register(signupRequest);
            ra.addFlashAttribute("message", "Успешно зарегистрированы, войдите в систему");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }

    /** Выход из системы: чистим куку */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
