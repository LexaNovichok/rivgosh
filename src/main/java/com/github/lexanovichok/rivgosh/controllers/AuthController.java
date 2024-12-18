package com.github.lexanovichok.rivgosh.controllers;

import com.github.lexanovichok.rivgosh.model.User;
import com.github.lexanovichok.rivgosh.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @GetMapping("/login")
    public String getLoginPage() {
        return "products/login_page";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "products/registration_page";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute User user) {
        userService.register(user);
        return "redirect:/login?success";
    }

}
