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
@CrossOrigin
public class PatientController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PatientController.class);

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
        // Logic to retrieve patient details based on id
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
        // Delegates update logic to the service
        Patient patientSaved = patientService.updatePatient(id, updatedPatient);
        log.info("Patient {} updated", id);
        return ResponseEntity.ok(patientSaved);
    }
}
