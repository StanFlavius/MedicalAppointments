package com.example.medicalappointments.controller;

import com.example.medicalappointments.service.DepartmentServiceImpl;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Setter
@RequestMapping("/departments")
@NoArgsConstructor
@Slf4j
public class DepartmentController {
    public final static String ALL_DEPARTMENTS = "departments";

    @Autowired
    private DepartmentServiceImpl departmentService;

    @GetMapping(value = {"", "/", "/index"})
    public String getAll(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return ALL_DEPARTMENTS;
    }
}
