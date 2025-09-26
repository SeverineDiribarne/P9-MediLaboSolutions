package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.model.Gender;
import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.services.patientService.IPatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "https://localhost:8090")
public class PatientController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PatientController.class);
    private static final String LOG_ERROR = "The patient could not be validated or registered in the database because the patient details were empty or partially empty,"
            +
            " with the exception of the address and telephone number, which are optional.";
    private static final String PATIENT_ADD = "patient/add";
    private static final String PATIENT_UPDATE = "patient/update";
    private static final String REDIRECT_PATIENT_LIST = "redirect:/patient/list";

    @Autowired
    IPatientService patientService;

    @RequestMapping("/list")
    public Iterable<Patient> home(Model model) {
        Iterable<Patient> patients = patientService.getPatientList();

        log.info("all patients are found and returned to view");
        return patients;
    }

    @PostMapping("/addpatient")
    public ResponseEntity<?> addPatient(@Valid @RequestBody Patient patient, Model model, BindingResult bindingResult) {
        // Vérifier les erreurs de validation
        // if (bindingResult.hasErrors()) {
        // log.error(LOG_ERROR);
        // model.addAttribute("errors", bindingResult.getAllErrors());
        // return PATIENT_ADD;
        // }

        // // check data valid and save to db, after saving return patient list OK
        // if( patient.getLastname().isEmpty() ) {
        // log.error(LOG_ERROR);
        // model.addAttribute("msgLastname" , "Your lastname is empty");
        // return PATIENT_ADD;
        // }
        // if(patient.getFirstname().isEmpty() ) {
        // log.error(LOG_ERROR);
        // model.addAttribute("msgFirstname", "Your firstname is empty");
        // return PATIENT_ADD;
        // }
        // if(patient.getBirthdate().isEmpty()) {
        // log.error(LOG_ERROR);
        // model.addAttribute("msgBirthdate", "Your birthdate is empty");
        // return PATIENT_ADD;
        // }
        // if(patient.getGender() != Gender.M && patient.getGender() != Gender.F &&
        // patient.getGender() != Gender.X) {
        // log.error(LOG_ERROR);
        // model.addAttribute("msgGender", "Your gender is incorrect");
        // return PATIENT_ADD;
        // }
        // if (Objects.equals(patient.getAddress(), "") || patient.getAddress()==null ||
        // Objects.equals(patient.getPhoneNumber(), "") || patient.getPhoneNumber() ==
        // null){
        // Patient savedPatient = patientService.savePatient(patient);
        // return REDIRECT_PATIENT_LIST;
        // }

        Patient savedPatient = patientService.savePatient(patient);
        return new ResponseEntity<Patient>(savedPatient,HttpStatus.OK);
    }

    @GetMapping("/details/{id}")
    public Optional<Patient> detailsOfPatient(@PathVariable long id) {
        // Logique pour récupérer les détails du patient en fonction de l'id
        Optional<Patient> patient = patientService.getPatientById(id);
        log.info(" patient is found and returned to view");
        return patient;

    }

    // TODO : A revoir cette methode du front vers le back
    @PostMapping("/update/{id}")
    public String updatePatientInformationValidate(@Valid @RequestParam long id, Model model,
            BindingResult bindingResult) {

        Optional<Patient> patient = patientService.getPatientById(id);

        // Vérifier les erreurs de validation
        if (bindingResult.hasErrors()) {
            log.error(LOG_ERROR);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return PATIENT_UPDATE;
        }

        if (patient.get().getLastname().isEmpty()) {
            log.error(LOG_ERROR);
            model.addAttribute("msgLastname", "Your lastname is empty");
            return PATIENT_UPDATE;
        }

        if (patient.get().getFirstname().isEmpty()) {
            log.error(LOG_ERROR);
            model.addAttribute("msgFirstname", "Your firstname is empty");
            return PATIENT_UPDATE;
        }

        if (patient.get().getBirthdate().isEmpty()) {
            log.error(LOG_ERROR);
            model.addAttribute("msgBirthdate", "Your birthdate is empty");
            return PATIENT_UPDATE;
        }

        if (patient.get().getGender() != Gender.M && patient.get().getGender() != Gender.F
                && patient.get().getGender() != Gender.X) {
            log.error(LOG_ERROR);
            model.addAttribute("msgGender", "Your gender is incorrect");
            return PATIENT_UPDATE;
        }
        Optional<Patient> patientById = patientService.getPatientById(patient.get().getPatientId());
        if (patientById.isPresent()) {
            Patient patientFoundById = patientById.get();
            Patient patientToUpdate = new Patient(patientFoundById.getPatientId(), patient.get().getLastname(),
                    patient.get().getFirstname(),
                    patient.get().getBirthdate(), patient.get().getGender(), patient.get().getAddress(),
                    patient.get().getPhoneNumber());
            Patient patientUpdated = patientService.savePatient(patientToUpdate);
            model.addAttribute("patientUpdated", patientUpdated);
        } else {
            log.error("Patient is not found.");
        }
        return REDIRECT_PATIENT_LIST;
    }
}
