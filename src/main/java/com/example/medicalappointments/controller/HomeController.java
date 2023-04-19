package com.example.medicalappointments.controller;

import com.example.medicalappointments.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    public final static String ACCESS_DENIED = "access-denied";

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        if (userService.isLoggedIn()) {
            return "redirect:/index";
        }

        return "login";
    }

    @GetMapping("/login-error")
    public String loginError() {
        return "login-error";
    }

    @GetMapping("/" + ACCESS_DENIED)
    public String accessDenied() {
        return "err_access_denied";
    }

    @GetMapping({"", "/", "/index"})
    public String home() {
        return "home";
    }
}
