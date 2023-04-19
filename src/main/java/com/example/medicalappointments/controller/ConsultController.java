package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.service.ConsultService;
import com.example.medicalappointments.service.DoctorService;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.medicalappointments.controller.DepartmentController.BINDING_RESULT_PATH;
import static com.example.medicalappointments.controller.DepartmentController.REDIRECT;

@Controller
@RequestMapping("/consults")
@RequiredArgsConstructor
@Slf4j
public class ConsultController {

    private final static String ALL_CONSULTS = "consults";

    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final ConsultService consultService;

    @GetMapping("/new")
    public String showConsultForm(Model model) {
        List<Department> allDepartments = departmentService.getAllDepartments();
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        Consult consult = new Consult();

        if (!model.containsAttribute("consult")) {
            Doctor doctor = new Doctor();
            doctor.setDepartment(new Department());
            consult.setDoctor(doctor);
            model.addAttribute("consult", consult);
        }

        model.addAttribute("allDepartments", allDepartments);
        model.addAttribute("allDoctors", allDoctors);
        model.addAttribute("doctorsDepartments", allDoctors.stream().map(doc ->
                Doctor.builder().id(doc.getId()).department(Department.builder().id(doc.getDepartment().getId()).build()).build()).collect(Collectors.toList()));

        return "consult_form";
    }

    @PostMapping
    public String createConsult(@ModelAttribute("consult") @Valid Consult consult, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            attr.addFlashAttribute(BINDING_RESULT_PATH + "consult", bindingResult);
            attr.addFlashAttribute("consult", consult);
            return REDIRECT + ALL_CONSULTS + "/new";
        }

        if (consult.getDoctor().getId() == null) {
            attr.addFlashAttribute("error_doctor", "A doctor must be selected!");
            attr.addFlashAttribute("consult", consult);
            return REDIRECT + ALL_CONSULTS + "/new";
        }

        try {
            consultService.saveConsult(consult);
        } catch (CustomException e) {
            log.info("Error when creating a new consult! Error message = " + e.getMessage());

            attr.addFlashAttribute(BINDING_RESULT_PATH + "consult", bindingResult);
            attr.addFlashAttribute("consult", consult);
            attr.addFlashAttribute("error_date", e.getMessage());

            return REDIRECT + ALL_CONSULTS + "/new";
        }
        return REDIRECT + ALL_CONSULTS;
    }
}
