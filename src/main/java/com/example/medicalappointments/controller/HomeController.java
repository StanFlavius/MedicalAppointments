package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class HomeController {

    public final static String ACCESS_DENIED = "access-denied";

    private final PatientService patientService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (!model.containsAttribute("patient")) {
                Patient patient = new Patient();
                patient.setUser(new User());
                model.addAttribute("patient", patient);
            } else {
                Patient patient = (Patient) model.getAttribute("patient");
                patient.getUser().setPassword("");
            }
            return "register";
        }
        return "redirect:/index";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        patientService.createPatient(patient);

        return "redirect:/login";
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/home";
    }
}
