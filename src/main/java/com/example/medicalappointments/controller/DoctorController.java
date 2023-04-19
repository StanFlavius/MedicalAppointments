package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.service.DoctorService;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static com.example.medicalappointments.controller.DepartmentController.BINDING_RESULT_PATH;
import static com.example.medicalappointments.controller.DepartmentController.REDIRECT;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {

    private final static String ALL_DOCTORS = "doctors";
    private final static String VIEW_DOCTOR = "doctor_info";
    private final static String EDIT_DOCTOR = "doctor_form";

    private final DoctorService doctorService;
    private final DepartmentService departmentService;

    @GetMapping
    public String getAll(Model model) {
        var doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return ALL_DOCTORS;
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") String doctorId, Model model) {
        var doctor = doctorService.findById(Long.valueOf(doctorId));
        model.addAttribute("doctor", doctor);
        return VIEW_DOCTOR;
    }

    @GetMapping("/{id}/edit")
    public String editDoctor(@PathVariable("id") String doctorId, Model model) {
        var doctor = doctorService.findById(Long.valueOf(doctorId));
        User user = doctor.getUser();

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", user);
        }
        if (!model.containsAttribute("doctor")) {
            model.addAttribute("doctor", doctor);
        }

        model.addAttribute("departmentAll", departmentService.getAllDepartments());

        return EDIT_DOCTOR;
    }

    @PostMapping
    public String saveOrUpdate(
            @ModelAttribute("user") @Valid User user, BindingResult bindingResultUser,
            @ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResultDoctor,
            RedirectAttributes attr) {

        if (bindingResultUser.hasErrors() || bindingResultDoctor.hasErrors()) {
            log.info("Model binding has errors!");

            attr.addFlashAttribute(BINDING_RESULT_PATH + "doctor", bindingResultDoctor);
            attr.addFlashAttribute("doctor", doctor);

            attr.addFlashAttribute(BINDING_RESULT_PATH + "user", bindingResultUser);
            attr.addFlashAttribute("user", user);

            return REDIRECT + ALL_DOCTORS + "/" + doctor.getId() + "/edit";
        }

        doctorService.saveOrUpdateUser(doctor, user);
        return REDIRECT + ALL_DOCTORS;
    }

    @GetMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctorById(id);
        return "redirect:/" + ALL_DOCTORS;
    }
}
