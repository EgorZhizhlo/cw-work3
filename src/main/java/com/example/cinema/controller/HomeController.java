package com.example.cinema.controller;

import com.example.cinema.dto.StatisticsDto;
import com.example.cinema.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final StatisticsService statisticsService;

    @Autowired
    public HomeController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/")
    public String index(Model model) {
        StatisticsDto stats = statisticsService.getStatistics();
        model.addAttribute("stats", stats);
        // можете добавить сюда контакты кинотеатра из пропертей или хардкодом
        model.addAttribute("cinemaName", "Ваш Кинотеатр");
        model.addAttribute("contactPhone", "+7 (123) 456-78-90");
        model.addAttribute("contactEmail", "info@cinema.ru");
        model.addAttribute("contactAddress", "г. Москва, ул. Пушкина, д. 10");
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
