package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.NotUniqueException;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.service.DoctorService;
import com.example.medicalappointments.service.RoleService;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Set;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_ADMIN;
import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_DOCTOR;
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
    private final static String NEW_DOCTOR = "doctor_form_new";

    private final RoleService roleService;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

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

    @GetMapping("/new")
    public String createDoctor(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        } else {
            User user = (User) model.getAttribute("user");
            user.setPassword("");
        }
        if (!model.containsAttribute("doctor")) {
            model.addAttribute("doctor", new Doctor());
        }
        model.addAttribute("password", "");
        model.addAttribute("departmentAll", departmentService.getAllDepartments());
        return NEW_DOCTOR;
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

    @PostMapping("/new")
    public String createDoctor(@ModelAttribute("user") @Valid User user, BindingResult bindingResultUser,
                               @ModelAttribute("doctor") @Valid Doctor doctor, BindingResult bindingResultDoctor,
                               @ModelAttribute("password") String password, RedirectAttributes attr) {
        if (bindingResultUser.hasErrors() || bindingResultDoctor.hasErrors()) {
            attr.addFlashAttribute(BINDING_RESULT_PATH + "user", bindingResultUser);
            attr.addFlashAttribute(BINDING_RESULT_PATH + "doctor", bindingResultDoctor);
            attr.addFlashAttribute("user", user);
            attr.addFlashAttribute("doctor", doctor);

            if (bindingResultUser.getFieldError("password") != null) {
                attr.addFlashAttribute("error_password", bindingResultUser.getFieldError("password").getDefaultMessage());
            }

            return REDIRECT + ALL_DOCTORS + "/new";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleService.getRoleByName(ROLE_DOCTOR));

        doctor.setUser(user);

        try {
            doctorService.createDoctor(doctor);
        } catch (NotUniqueException e) {
            attr.addFlashAttribute(BINDING_RESULT_PATH + "user", bindingResultUser);
            attr.addFlashAttribute(BINDING_RESULT_PATH + "doctor", bindingResultDoctor);
            attr.addFlashAttribute("user", user);
            attr.addFlashAttribute("doctor", doctor);

            if (e.getConflictingField() == NotUniqueException.ConflictingField.EMAIL) {
                attr.addFlashAttribute("error_email", e.getMessage());
            }
            if (e.getConflictingField() == NotUniqueException.ConflictingField.USERNAME) {
                attr.addFlashAttribute("error_username", e.getMessage());
            }
            return REDIRECT + ALL_DOCTORS + "/new";
        }
        return REDIRECT + ALL_DOCTORS;
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
        return REDIRECT + ALL_DOCTORS;
    }
}
