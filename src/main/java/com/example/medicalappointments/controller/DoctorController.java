package com.example.medicalappointments.controller;

import com.example.medicalappointments.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final static String ALL_DOCTORS = "doctors";

    private final DoctorService doctorService;

    @GetMapping
    public String getAll(Model model) {
        var doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return ALL_DOCTORS;
    }
}
