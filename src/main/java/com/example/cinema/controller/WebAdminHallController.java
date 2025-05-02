package com.example.cinema.controller;

import com.example.cinema.dto.HallDto;
import com.example.cinema.service.HallService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/halls")
public class WebAdminHallController {

    private final HallService hallService;

    @Autowired
    public WebAdminHallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("halls", hallService.getAllHalls());
        return "admin/admin-halls";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("hall", new HallDto());
        return "admin/admin-hall-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("hall", hallService.getHallById(id));
        return "admin/admin-hall-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("hall") HallDto dto,
                       BindingResult br,
                       RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "admin/admin-hall-form";
        }
        if (dto.getId() == null) {
            hallService.createHall(dto);
            ra.addFlashAttribute("message", "Зал создан");
        } else {
            hallService.updateHall(dto.getId(), dto);
            ra.addFlashAttribute("message", "Зал обновлён");
        }
        return "redirect:/admin/halls";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        hallService.deleteHall(id);
        ra.addFlashAttribute("message", "Зал удалён");
        return "redirect:/admin/halls";
    }
}
