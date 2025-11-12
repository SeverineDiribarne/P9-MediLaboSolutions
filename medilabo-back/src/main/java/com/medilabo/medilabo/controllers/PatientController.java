package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.exceptions.PatientNotFoundException;
import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.services.patientService.IPatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "https://localhost:8090")
public class PatientController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PatientController.class);
    // private static final String LOG_ERROR = "The patient could not be validated
    // or registered in the database because the patient details were empty or
    // partially empty,"
    // +
    // " with the exception of the address and telephone number, which are
    // optional.";
    // private static final String PATIENT_ADD = "patient/add";
    // private static final String PATIENT_UPDATE = "patient/update";
    private static final String REDIRECT_PATIENT_LIST = "redirect:/patient/list";

    @Autowired
    IPatientService patientService;

    @GetMapping("/list")
    public Iterable<Patient> home(Model model) {
        Iterable<Patient> patients = patientService.getPatientList();
        log.info("all patients are found and returned to view");
        return patients;
    }

    @PostMapping("/addpatient")
    public ResponseEntity<?> addPatient(@Valid @RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(patient);
        return new ResponseEntity<Patient>(savedPatient, HttpStatus.OK);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Patient> detailsOfPatient(@PathVariable Long id) {
        // Logique pour récupérer les détails du patient en fonction de l'id
        return patientService.getPatientById(id)
                .map(patient -> {
                    log.info("Patient {} found", id);
                    return ResponseEntity.ok(patient);
                })
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient updatedPatient) {
        Optional<Patient> patientToUpdate = patientService.getPatientById(id);
        if (patientToUpdate.isEmpty()) {
            log.error("Patient {} not found for update", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        }
        // Délègue la logique de mise à jour au service
        Patient patientSaved = patientService.updatePatient(id, updatedPatient);
        log.info("Patient {} updated", id);
        return ResponseEntity.ok(patientSaved);
    }
}
