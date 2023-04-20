package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.service.DepartmentServiceImpl;
import com.example.medicalappointments.service.DoctorService;
import com.example.medicalappointments.service.MedicalProcedureService;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@Setter
@RequestMapping("/departments")
@NoArgsConstructor
@Slf4j
public class DepartmentController {
    public final static String REDIRECT = "redirect:/";
    public final static String BINDING_RESULT_PATH = "org.springframework.validation.BindingResult.";

    public final static String ALL_DEPARTMENTS = "departments";
    private final static String ADD_EDIT_DEPARTMENT = "department_form";
    public final static String VIEW_DEPARTMENT = "department_info";

    @Autowired
    private DepartmentServiceImpl departmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private MedicalProcedureService medicalProcedureService;

    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return ALL_DEPARTMENTS;
    }

    @GetMapping("/new")
    public String addDepartment(Model model) {
        if (!model.containsAttribute("department")) {
            model.addAttribute("department", new Department());
        }
        return ADD_EDIT_DEPARTMENT;
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") String departmentId, Model model){
        var department = departmentService.getDepartmentById(Long.valueOf(departmentId));
        model.addAttribute("department", department);

        var doctors = doctorService.getDoctorsByDepartment(Long.valueOf(departmentId));
        model.addAttribute("doctors", doctors);

        var procedures = medicalProcedureService.getProceduresByDepartment(Long.valueOf(departmentId));
        model.addAttribute("procedures", procedures);
        return VIEW_DEPARTMENT;
    }

    @GetMapping("/{id}/edit")
    public String editDepartment(@PathVariable("id") String departmentId, Model model) {
        var department = departmentService.getDepartmentById(Long.valueOf(departmentId));

        if (!model.containsAttribute("department")) {
            model.addAttribute("department", department);
        }
        return ADD_EDIT_DEPARTMENT;
    }

    @PostMapping
    public String saveOrUpdateDepartment(@ModelAttribute("department") @Valid Department department, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            log.info("Model binding has errors!");

            attr.addFlashAttribute(BINDING_RESULT_PATH + "department", bindingResult);
            attr.addFlashAttribute("department", department);

            if (department.getId() != null) {
                log.info(String.format("Redirected back to endpoint %s", ALL_DEPARTMENTS + "/" + department.getId() + "/edit"));
                return REDIRECT + ALL_DEPARTMENTS + "/" + department.getId() + "/edit";
            } else {
                log.info(String.format("Redirected back to endpoint %s", ALL_DEPARTMENTS + "/new"));
                return REDIRECT + ALL_DEPARTMENTS + "/new";
            }
        }

        try {
            departmentService.saveDepartment(department);
        } catch (CustomException e) {
            log.info("Error when saving into database! Error message = " + e.getMessage());

            attr.addFlashAttribute(BINDING_RESULT_PATH + "department", bindingResult);
            attr.addFlashAttribute("department", department);
            attr.addFlashAttribute("error_department", e.getMessage());

            if (department.getId() == null) {
                log.info(String.format("Redirected back to endpoint %s", ALL_DEPARTMENTS + "/new"));
                return REDIRECT + ALL_DEPARTMENTS + "/new";
            } else {
                log.info(String.format("Redirected back to endpoint %s", ALL_DEPARTMENTS + "/" + department.getId() + "/edit"));
                return REDIRECT + ALL_DEPARTMENTS + "/" + department.getId() + "/edit";
            }
        }

        return REDIRECT + ALL_DEPARTMENTS;
    }
}
