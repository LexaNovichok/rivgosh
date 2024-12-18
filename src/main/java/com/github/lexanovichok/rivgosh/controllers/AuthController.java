package com.github.lexanovichok.rivgosh.controllers;

import com.github.lexanovichok.rivgosh.model.User;
import com.github.lexanovichok.rivgosh.services.AuthenticationService;
import com.github.lexanovichok.rivgosh.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        userService.registerUser(user);
        model.addAttribute("success", "Регистрация прошла успешно!");
        return "redirect:/products";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Очищаем контекст безопасности
        SecurityContextHolder.clearContext();

        // Возвращаем пользователя на главную страницу или страницу входа
        return "redirect:/index.html";  // Перенаправляем на страницу логина или на любую другую страницу
    }

//    @GetMapping("/account")
//    public String account(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
//
//        model.addAttribute("isAuthenticated", isAuthenticated);
//        return "account";
//    }

    @GetMapping("/account")
    public String account(HttpServletRequest request, Model model) {
        // Проверяем, если пользователь аутентифицирован (например, сессия или токен)
        HttpSession session = request.getSession(false);
        boolean isAuthenticated = session != null && session.getAttribute("username") != null;

        model.addAttribute("isAuthenticated", isAuthenticated);
        return "account";  // Возвращаем имя шаблона
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model, HttpServletRequest request) {
        if (userService.authenticate(username, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            return "redirect:/products";
        } else {
            // Если аутентификация не удалась
            model.addAttribute("error", "Invalid username or password");
            return "redirect:/products";
        }
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
