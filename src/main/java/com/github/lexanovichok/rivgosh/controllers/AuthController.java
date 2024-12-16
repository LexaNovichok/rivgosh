package com.github.lexanovichok.rivgosh.controllers;

import com.github.lexanovichok.rivgosh.model.User;
import com.github.lexanovichok.rivgosh.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }



    // Обработка формы регистрации
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        // Пытаемся сохранить пользователя
        boolean isRegistered = userService.registerUser(user);
        if (!isRegistered) {
            model.addAttribute("error", "Пользователь с таким email уже существует!");
            return "register";
        }

        model.addAttribute("success", "Регистрация прошла успешно!");
        return "login"; // Перенаправляем на страницу входа
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Welcome, Admin!";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint() {
        return "Welcome, User!";
    }
}
