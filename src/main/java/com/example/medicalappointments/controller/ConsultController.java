package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.Medication;
import com.example.medicalappointments.service.ConsultService;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.example.medicalappointments.controller.DepartmentController.BINDING_RESULT_PATH;
import static com.example.medicalappointments.controller.DepartmentController.REDIRECT;
import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/consults")
@RequiredArgsConstructor
@Slf4j
public class ConsultController {

    private final static String ALL_CONSULTS = "consults";
    private final static String VIEW_CONSULT = "consult_info";

    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final ConsultService consultService;

    @GetMapping("/new")
    public String showConsultForm(@RequestParam(value = "department", required = false) Long departmentId,
                                  @RequestParam(value = "doctor", required = false) Long doctorId,
                                  Model model, RedirectAttributes attr) {
        List<Department> allDepartments = departmentService.getAllDepartments();
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        Consult consult = new Consult();

        if (!model.containsAttribute("consult")) {
            Doctor doctor = new Doctor();
            doctor.setDepartment(new Department());
            consult.setDoctor(doctor);
            model.addAttribute("consult", consult);
        }

        if (departmentId != null || doctorId != null) {
            attr.addFlashAttribute("consult", consult);
            attr.addFlashAttribute("selectedDepartment", departmentId);
            attr.addFlashAttribute("selectedDoctor", doctorId);
            attr.addFlashAttribute("allDepartments", allDepartments);
            attr.addFlashAttribute("allDoctors", allDoctors);
            attr.addFlashAttribute("doctorsDepartments", getDoctorsDepartments(allDoctors));
            return REDIRECT + ALL_CONSULTS + "/new";
        }

        model.addAttribute("allDepartments", allDepartments);
        model.addAttribute("allDoctors", allDoctors);
        model.addAttribute("doctorsDepartments", getDoctorsDepartments(allDoctors));

        return "consult_form";
    }

    private List<Doctor> getDoctorsDepartments(List<Doctor> doctors) {
        return doctors.stream()
                .map(doc -> Doctor.builder()
                        .id(doc.getId())
                        .department(Department.builder()
                                .id(doc.getDepartment().getId())
                                .build())
                        .build())
                .collect(toList());
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

    @GetMapping
    public String getAll(Model model) {
        List<Consult> consults = consultService.getAllConsults();
        List<Consult> futureConsults = consults.stream()
                .filter(consult -> consult.getDate().after(new Date(System.currentTimeMillis())))
                .collect(toList());
        List<Consult> pastConsults = consults.stream()
                .filter(consult -> consult.getDate().before(new Date(System.currentTimeMillis())))
                .collect(toList());
        model.addAttribute("futureConsults", futureConsults);
        model.addAttribute("pastConsults", pastConsults);
        return ALL_CONSULTS;
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") String consultId, Model model) {
        var consult = consultService.getConsultById(Long.valueOf(consultId));
        var selectedMedicationIds = consult.getMedications().stream().map(Medication::getId).collect(toList());
        var medications = consult.getMedications().stream()
                .sorted(Comparator.comparing(Medication::getName).thenComparing(Medication::getQuantity))
                .collect(toList());
        var doctor = consult.getDoctor();
        var patient = consult.getPatient();

        var doctorName = doctor.getUser().getLastName() + " " + doctor.getUser().getFirstName();
        var patientName = patient.getUser().getLastName() + " " + patient.getUser().getFirstName();

        model.addAttribute("consult", consult);
        model.addAttribute("doctorName", doctorName);
        model.addAttribute("patientName", patientName);
        model.addAttribute("medicationAll", medications);
        model.addAttribute("selectedMedicationIds", selectedMedicationIds);

        return VIEW_CONSULT;
    }
}
