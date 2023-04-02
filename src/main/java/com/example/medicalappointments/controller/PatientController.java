package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.NotUniqueException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

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
    public String register(@Valid @ModelAttribute Patient patient, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            patientService.createPatient(patient);
        } catch (NotUniqueException e) {
            attr.addFlashAttribute("patient", patient);

            switch (e.getConflictingField()) {
                case USERNAME:
                    attr.addFlashAttribute("error_username", e.getMessage());
                    break;
                case EMAIL:
                    attr.addFlashAttribute("error_email", e.getMessage());
                    break;
                case CNP:
                    attr.addFlashAttribute("error_cnp", e.getMessage());
            }
            return "redirect:/register";
        }

        return "redirect:/login";
    }
}
