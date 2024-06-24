package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.model.Gender;
import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.services.IPatientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api")
public class PatientController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PatientController.class);
    private static final String LOG_ERROR = "The patient could not be validated or registered in the database because the patient details were empty or partially empty," +
                                            " with the exception of the address and telephone number, which are optional.";
    private static final String PATIENT_ADD = "patient/add";
    private static final String PATIENT_UPDATE = "patient/update";
    private static final String REDIRECT_PATIENT_LIST = "redirect:/patient/list";


    @Autowired
    IPatientService patientService;

    @RequestMapping("/patient/list")
    public Iterable<Patient> home(Model model) {
        Iterable<Patient> patients = patientService.getPatientList();

        log.info("all patients are found and returned to view");
        return patients;
    }


    @PostMapping("/patient/validate")
    public String addPatientInformationValidate(@Valid @RequestBody Patient patient, Model model, BindingResult bindingResult) {

        // Vérifier les erreurs de validation
        if (bindingResult.hasErrors()) {
            log.error(LOG_ERROR);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return PATIENT_ADD;
        }

        // check data valid and save to db, after saving return patient list OK
        if( patient.getLastname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgLastname" , "Your lastname is empty");
            return PATIENT_ADD;
        }
        if(patient.getFirstname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgFirstname", "Your firstname is empty");
            return PATIENT_ADD;
        }
        if(patient.getBirthdate().isEmpty()) {
            log.error(LOG_ERROR);
            model.addAttribute("msgBirthdate", "Your birthdate is empty");
            return "/patient/add";
        }
        if(patient.getGender() != Gender.M && patient.getGender() != Gender.F && patient.getGender() != Gender.X) {
            log.error(LOG_ERROR);
            model.addAttribute("msgGender", "Your gender is incorrect");
            return PATIENT_ADD;
        }

        Patient newPatient = patientService.savePatient(patient);
        return REDIRECT_PATIENT_LIST;
    }

    @PostMapping("/patient/update")
    public String updatePatientInformationValidate( @RequestBody Patient patient, Model model, BindingResult bindingResult) {

        // Vérifier les erreurs de validation
        if (bindingResult.hasErrors()) {
            log.error(LOG_ERROR);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return PATIENT_UPDATE;
        }

        if( patient.getLastname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgLastname" , "Your lastname is empty");
            return PATIENT_UPDATE;
        }

        if(patient.getFirstname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgFirstname", "Your firstname is empty");
            return PATIENT_UPDATE;
        }

        if(patient.getBirthdate().isEmpty()) {
            log.error(LOG_ERROR);
            model.addAttribute("msgBirthdate", "Your birthdate is empty");
            return PATIENT_UPDATE;
        }

        if(patient.getGender() != Gender.M && patient.getGender() != Gender.F && patient.getGender() != Gender.X) {
            log.error(LOG_ERROR);
            model.addAttribute("msgGender", "Your gender is incorrect");
            return PATIENT_UPDATE;
        }

        Optional<Patient> patientByFullname = patientService.getPatientByFullname(patient.getLastname() + " " + patient.getFirstname());
        if(patientByFullname.isPresent()) {
            Patient patientFoundByName = patientByFullname.get();
            Patient patientToUpdate = new Patient(patientFoundByName.getId(), patient.getLastname(), patient.getFirstname(),
                                        patient.getBirthdate(), patient.getGender(), patient.getAddress(),
                                        patient.getPhoneNumber());
            Patient patientUpdated = patientService.savePatient(patientToUpdate);
            model.addAttribute("patientUpdated",patientUpdated);
        }
        else{
            log.error("Patient is not found.");
        }
        return REDIRECT_PATIENT_LIST;
    }
}
