package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.MedicalProcedure;
import com.example.medicalappointments.service.MedicalProcedureService;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static com.example.medicalappointments.controller.DepartmentController.*;

@Controller
@RequestMapping("/procedures")
@RequiredArgsConstructor
@Slf4j
public class MedicalProcedureController {

    private final MedicalProcedureService procedureService;
    private final DepartmentService departmentService;

    @PostMapping("/{departmentId}")
    public String add(@PathVariable("departmentId") Long departmentId,
                      @ModelAttribute("newProcedure") @Valid MedicalProcedure newProcedure, BindingResult bindingResultNewProcedure,
                      RedirectAttributes attr) {

        if (bindingResultNewProcedure.hasErrors()) {
            log.info("Model binding has errors!");

            attr.addFlashAttribute(BINDING_RESULT_PATH + "newProcedure", bindingResultNewProcedure);
            attr.addFlashAttribute("newProcedure", newProcedure);

            log.info(String.format("Redirected back to endpoint %s", ALL_DEPARTMENTS + "/" + departmentId + "/edit"));
            return REDIRECT + ALL_DEPARTMENTS + "/" + departmentId + "/edit";
        }

        try {
            procedureService.save(departmentId, newProcedure);
        } catch (Exception e) {
            attr.addFlashAttribute(BINDING_RESULT_PATH + "newProcedure", bindingResultNewProcedure);
            attr.addFlashAttribute("newProcedure", newProcedure);
        }
        return REDIRECT + ALL_DEPARTMENTS + "/" + departmentId + "/edit";
    }

    @GetMapping("/{id}/delete")
    public String deleteProcedure(@PathVariable("id") Long id) {
        Long departmentId = procedureService.findById(id).getDepartment().getId();
        procedureService.deleteById(id);
        return REDIRECT + ALL_DEPARTMENTS + "/" + departmentId + "/edit";
    }

}
