package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.NotUniqueException;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.service.ConsultService;
import com.example.medicalappointments.service.PatientService;
import com.sun.istack.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static com.example.medicalappointments.controller.DepartmentController.BINDING_RESULT_PATH;
import static com.example.medicalappointments.controller.DepartmentController.REDIRECT;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final ConsultService consultService;
    private final static String ALL_PATIENTS = "patients";
    private final static String VIEW_PATIENT = "patient_info";
    private final static String EDIT_PATIENT = "patient_form";

    @GetMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatientById(id);
        return REDIRECT + ALL_PATIENTS;
    }

    @GetMapping("/patients/{id}/edit")
    public String editPatientPersonalInfo(@PathVariable Long id, Model model) {
        var patient = patientService.findById(id);
        User user = patient.getUser();

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", user);
        }
        if (!model.containsAttribute("patient")) {
            model.addAttribute("patient", patient);
        }

      return EDIT_PATIENT;
    }

    @PostMapping("/patients")
    public String saveOrUpdate(
            @ModelAttribute("user") @Valid User user, BindingResult bindingResultUser,
            @ModelAttribute("patient") @Valid Patient patient, BindingResult bindingResultPatient,
            RedirectAttributes attr) {

        if (bindingResultUser.hasErrors() || bindingResultPatient.hasErrors()) {
            log.info("Model binding has errors!");

            attr.addFlashAttribute(BINDING_RESULT_PATH + "patient", bindingResultPatient);
            attr.addFlashAttribute("patient", patient);

            attr.addFlashAttribute(BINDING_RESULT_PATH + "user", bindingResultUser);
            attr.addFlashAttribute("user", user);

            return REDIRECT + ALL_PATIENTS + "/" + patient.getId() + "/edit";
        }

        patientService.saveOrUpdateUser(patient, user);
        return REDIRECT + ALL_PATIENTS;
    }

    @GetMapping("/patients")
    public String getAll(Model model) {
        var patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return ALL_PATIENTS;
    }

    @GetMapping("/patients/{id}")
    public String getById(@PathVariable("id") String patientId, Model model) {
        var patient = patientService.findById(Long.valueOf(patientId));
        model.addAttribute("patient", patient);

        var consults = consultService.getConsultsByPatientId(Long.valueOf(patientId));
        model.addAttribute("consults", consults);
        return VIEW_PATIENT;
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
