package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.Medication;
import com.example.medicalappointments.service.MedicationService;
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
@RequestMapping("/medications")
@RequiredArgsConstructor
@Slf4j
public class MedicationController {
    private final static String ALL_MEDICATIONS = "medications";
    private final static String ADD_EDIT_MEDICATION = "medication_form";

    private final MedicationService medicationService;

    @GetMapping("/new")
    public String addMedication(Model model) {
        if (!model.containsAttribute("medication")) {
            model.addAttribute("medication", new Medication());
        }

        return ADD_EDIT_MEDICATION;
    }

    @GetMapping
    public String getAll(Model model) {
        var medications = medicationService.getAllMedications();
        model.addAttribute("medications", medications);
        return ALL_MEDICATIONS;
    }

    @GetMapping("/{id}/edit")
    public String editMedication(@PathVariable("id") String medicationId, Model model) {
        var medication = medicationService.getMedicationById(Long.valueOf(medicationId));

        if (!model.containsAttribute("medication")) {
            model.addAttribute("medication", medication);
        }

        return ADD_EDIT_MEDICATION;
    }

    @PostMapping
    public String saveOrUpdate(@ModelAttribute("medication") @Valid Medication medication, BindingResult bindingResult,
                               RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            log.info("Model binding has errors!");

            attr.addFlashAttribute(BINDING_RESULT_PATH + "medication", bindingResult);
            attr.addFlashAttribute("medication", medication);
            return ADD_EDIT_MEDICATION;

//            if (medication.getId() != null) {
//                log.info(String.format("Redirected back to endpoint %s", ALL_MEDICATIONS + "/" + medication.getId() + "/edit"));
//                return REDIRECT + ALL_MEDICATIONS + "/" + medication.getId() + "/edit";
//            } else {
//                log.info(String.format("Redirected back to endpoint %s", ALL_MEDICATIONS + "/new"));
//                return REDIRECT + ALL_MEDICATIONS + "/new";
//            }
        }

        try {
            medicationService.saveMedication(medication);
        } catch (CustomException e) {
            log.info("Error when saving into database! Error message = " + e.getMessage());

            attr.addFlashAttribute(BINDING_RESULT_PATH + "medication", bindingResult);
            attr.addFlashAttribute("medication", medication);
            attr.addFlashAttribute("error_medication", e.getMessage());

            if (medication.getId() == null) {
                log.info(String.format("Redirected back to endpoint %s", ALL_MEDICATIONS + "/new"));
                return REDIRECT + ALL_MEDICATIONS + "/new";
            } else {
                log.info(String.format("Redirected back to endpoint %s", ALL_MEDICATIONS + "/" + medication.getId() + "/edit"));
                return REDIRECT + ALL_MEDICATIONS + "/" + medication.getId() + "/edit";
            }
        }

        return REDIRECT + ALL_MEDICATIONS;
    }
}
