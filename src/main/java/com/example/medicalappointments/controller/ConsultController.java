package com.example.medicalappointments.controller;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.*;
import com.example.medicalappointments.model.dto.SelectedMedication;
import com.example.medicalappointments.service.*;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_DOCTOR;
import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_PATIENT;
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
    private final PatientService patientService;
    private final UserService userService;
    private final MedicationService medicationService;
    private final MedicalProcedureService medicalProcedureService;

    @GetMapping("/new")
    public String showConsultForm(@RequestParam(value = "department", required = false) Long departmentId,
                                  @RequestParam(value = "doctor", required = false) Long doctorId,
                                  Model model, RedirectAttributes attr) {
        List<Department> allDepartments = departmentService.getAllDepartments();
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        List<Patient> allPatients = patientService.getAllPatients();
        List<SelectedMedication> selectedMedications;

        Consult consult = new Consult();

        if (!model.containsAttribute("consult")) {
            Doctor doctor = new Doctor();
            doctor.setDepartment(new Department());
            consult.setDoctor(doctor);
            consult.setMedications(new ArrayList<>());
            consult.setMedicalProcedure(new MedicalProcedure());
            selectedMedications = medicationService.getAllMedications().stream()
                    .map(med -> new SelectedMedication(med, false))
                    .collect(Collectors.toList());
            model.addAttribute("consult", consult);
        } else {
            consult = (Consult) model.getAttribute("consult");
            var containedMedicationIds = consult.getMedications() == null ? new ArrayList<Long>() : consult.getMedications().stream().map(Medication::getId).collect(Collectors.toList());
            selectedMedications = medicationService.getAllMedications().stream().map(med -> {
                var isContained = containedMedicationIds.contains(med.getId());
                return new SelectedMedication(med, isContained);
            }).collect(Collectors.toList());
            consult.setMedications(medicationService.findMedicationsByIdContains(containedMedicationIds));
        }

        model.addAttribute("selectedMedications", selectedMedications);

        if (departmentId != null || doctorId != null) {
            attr.addFlashAttribute("consult", consult);
            attr.addFlashAttribute("selectedDepartment", departmentId);
            attr.addFlashAttribute("selectedDoctor", doctorId);
            attr.addFlashAttribute("allDepartments", allDepartments);
            attr.addFlashAttribute("allDoctors", allDoctors);
            attr.addFlashAttribute("doctorsDepartments", getDoctorsDepartments(allDoctors));
            return REDIRECT + ALL_CONSULTS + "/new";
        }

        if (userService.isCurrentUserDoctor()) {
            Doctor currentDoctor = userService.getDoctorForCurrentUser();
            model.addAttribute("allProcedures", medicalProcedureService.getProceduresByDepartment(currentDoctor.getDepartment().getId()));
        }
        model.addAttribute("allPatients", allPatients);
        model.addAttribute("allDepartments", allDepartments);
        model.addAttribute("allDoctors", allDoctors);
        model.addAttribute("doctorsDepartments", getDoctorsDepartments(allDoctors));
        return "consult_form";
    }

    @GetMapping("/{id}/edit")
    public String editConsult(@PathVariable("id") String consultId, Model model) {
        List<Department> allDepartments = departmentService.getAllDepartments();
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        List<Patient> allPatients = patientService.getAllPatients();
        List<SelectedMedication> selectedMedications;
        Consult consult;

        /* First time display, no validation failed before */
        if (!model.containsAttribute("consult")) {
            consult = consultService.getConsultById(Long.valueOf(consultId));
            var containedMedicationIds = consult.getMedications() == null ? new ArrayList<Long>() : consult.getMedications().stream().map(Medication::getId).collect(Collectors.toList());
            selectedMedications = medicationService.getAllMedications().stream().map(med -> {
                var isContained = containedMedicationIds.contains(med.getId());
                return new SelectedMedication(med, isContained);
            }).collect(Collectors.toList());
            model.addAttribute("consult", consult);
        } else {
            consult = (Consult) model.getAttribute("consult");
            var containedMedicationIds = consult.getMedications() == null ? new ArrayList<Long>() : consult.getMedications().stream().map(Medication::getId).collect(Collectors.toList());
            selectedMedications = medicationService.getAllMedications().stream().map(med -> {
                var isContained = containedMedicationIds.contains(med.getId());
                return new SelectedMedication(med, isContained);
            }).collect(Collectors.toList());
            consult.setMedications(medicationService.findMedicationsByIdContains(containedMedicationIds));
        }

        if (userService.isCurrentUserDoctor()) {
            Doctor currentDoctor = userService.getDoctorForCurrentUser();
            model.addAttribute("allProcedures", medicalProcedureService.getProceduresByDepartment(currentDoctor.getDepartment().getId()));
        }
        model.addAttribute("selectedMedications", selectedMedications);
        model.addAttribute("allPatients", allPatients);
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
    public String createOrUpdateConsult(@ModelAttribute("consult") @Valid Consult consult, BindingResult bindingResult, RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            attr.addFlashAttribute(BINDING_RESULT_PATH + "consult", bindingResult);
            attr.addFlashAttribute("consult", consult);
            if (consult.getId() != null) {
                return REDIRECT + ALL_CONSULTS + "/" + consult.getId() + "/edit";
            } else {
                return REDIRECT + ALL_CONSULTS + "/new";
            }
        }

        if (userService.hasRole(ROLE_PATIENT) && (consult.getDoctor() == null || consult.getDoctor().getId() == null)) {
            attr.addFlashAttribute("error_doctor", "A doctor must be selected!");
            attr.addFlashAttribute("consult", consult);
            if (consult.getId() != null) {
                return REDIRECT + ALL_CONSULTS + "/" + consult.getId() + "/edit";
            } else {
                return REDIRECT + ALL_CONSULTS + "/new";
            }
        }

        if (userService.hasRole(ROLE_DOCTOR) && (consult.getPatient() == null || consult.getPatient().getId() == null)) {
            attr.addFlashAttribute("error_patient", "A patient must be selected!");
            attr.addFlashAttribute("consult", consult);
            if (consult.getId() != null) {
                return REDIRECT + ALL_CONSULTS + "/" + consult.getId() + "/edit";
            } else {
                return REDIRECT + ALL_CONSULTS + "/new";
            }
        }

        try {
            consultService.saveOrUpdateConsult(consult);
        } catch (CustomException e) {
            log.info("Error when creating a new consult! Error message = " + e.getMessage());

            attr.addFlashAttribute(BINDING_RESULT_PATH + "consult", bindingResult);
            attr.addFlashAttribute("consult", consult);
            attr.addFlashAttribute("error_date", e.getMessage());

            if (consult.getId() != null) {
                return REDIRECT + ALL_CONSULTS + "/" + consult.getId() + "/edit";
            } else {
                return REDIRECT + ALL_CONSULTS + "/new";
            }
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

    @GetMapping("/{id}/delete")
    public String deleteConsult(@PathVariable Long id, RedirectAttributes attr) {
        try {
            consultService.deleteConsultById(id);
        } catch (CustomException e) {
            attr.addFlashAttribute("error_delete_consult", e.getMessage());
        }
        return REDIRECT + ALL_CONSULTS;
    }
}
