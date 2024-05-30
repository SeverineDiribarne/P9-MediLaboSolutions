package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.model.Gender;
import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.services.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
public class PatientController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PatientController.class);
    private static final String LOG_ERROR = "The patient could not be validated or registered in the database because the patient details were empty or partially empty," +
                                            " with the exception of the address and telephone number, which are optional.";

    @Autowired
    IPatientService patientService;

    @RequestMapping("/patient/list")
    public String home(Model model) {
        Iterable<Patient> patients = patientService.getPatientList();
        model.addAttribute("patients", patients);
        log.info("all patients are found and returned to view");
        return "patient/list";
    }
    @GetMapping("/patient/add")
    public String showAddPatientForm(Patient patient, Model model){
        model.addAttribute("patient", patient);
        log.info("The display of the addPatient page of a patient is functional");
        return "/patient/add";
    }

    @PostMapping("/patient/validate")
    public String addPatientInformationValidate(Patient patient,  Model model) {

        // check data valid and save to db, after saving return patient list OK
        if( patient.getLastname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgLastname" , "Your lastname is empty");
            return "/patient/add";
        }
        if(patient.getFirstname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgFirstname", "Your firstname is empty");
            return "/patient/add";
        }
        if(patient.getBirthdate().isEmpty()) {
            log.error(LOG_ERROR);
            model.addAttribute("msgBirthdate", "Your birthdate is empty");
            return "/patient/add";
        }
        if(patient.getGender() != Gender.MAN && patient.getGender() != Gender.WOMAN && patient.getGender() != Gender.X) {
            log.error(LOG_ERROR);
            model.addAttribute("msgGender", "Your gender is incorrect");
            return "/patient/add";
        }
        Patient newPatient = patientService.savePatient(patient);
        model.addAttribute("newPatient", newPatient);
        return "redirect:patient/list";
    }

    @GetMapping ("/patient/update")
    public String showUpdatePatientForm(@PathVariable("name") String name, Patient patient, Model model){
        model.addAttribute("patient", patient);
        log.info("The display of the updatePatient page of a patient is functional");
        return "patient/update";
    }

    @PostMapping("/patient/update/{name}")
    public String updatePatientInformationValidate(Patient patient, Model model) {

        if( patient.getLastname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgLastname" , "Your lastname is empty");
            return "/patient/update";
        }
        if(patient.getFirstname().isEmpty() ) {
            log.error(LOG_ERROR);
            model.addAttribute("msgFirstname", "Your firstname is empty");
            return "/patient/update";
        }
        if(patient.getBirthdate().isEmpty()) {
            log.error(LOG_ERROR);
            model.addAttribute("msgBirthdate", "Your birthdate is empty");
            return "/patient/update";
        }
        if(patient.getGender() != Gender.MAN && patient.getGender() != Gender.WOMAN && patient.getGender() != Gender.X) {
            log.error(LOG_ERROR);
            model.addAttribute("msgGender", "Your gender is incorrect");
            return "/patient/update";
        }
        Optional<Patient> patientByName = patientService.getPatientByName(patient.getLastname());
        if(patientByName.isPresent()) {
            Patient patientFoundByName = patientByName.get();
            Patient patientToUpdate = new Patient(patientFoundByName.getId(), patient.getLastname(), patient.getFirstname(),
                                        patient.getBirthdate(), patient.getGender(), patient.getAddress(),
                                        patient.getPhoneNumber());
            Patient patientUpdated = patientService.savePatient(patientToUpdate);
            model.addAttribute("patientUpdated",patientUpdated);
        }
        else{
            log.error("Patient is not found.");
        }
        return "redirect:patient/list";
    }
}
