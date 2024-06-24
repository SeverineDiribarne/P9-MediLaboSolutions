package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.model.Patient;
import com.medilabo_gui.medilabo_gui.services.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class PatientController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PatientController.class);
    private static final String LOG_ERROR = "The patient could not be validated or registered in the database because the patient details were empty or partially empty," +
            " with the exception of the address and telephone number, which are optional.";
    private static final String PATIENT_ADD = "patient/add";
    private static final String PATIENT_UPDATE = "patient/update";
    private static final String REDIRECT_PATIENT_LIST = "redirect:/patient/list";

    @Autowired
    IPatientService patientService;

//    @GetMapping("/")
//    public String home() {
//        return "list";
//    }

    @GetMapping("/patient/list")
    public String listPatients(Model model) {
       // Iterable<Patient> patients = patientService.getPatientList();
      //  model.addAttribute("patients", patients);
        ResponseEntity<List<Patient>> patients = patientService.getPatientList();
        model.addAttribute("patients", patients.getBody());
        log.info("all patients are found and returned to view");
        return "list";
    }
    @GetMapping("/patient/add")
    public String showAddPatientForm(Patient patient, Model model){
        model.addAttribute("patient", patient);
        log.info("The display of the addPatient page of a patient is functional");
        return PATIENT_ADD;
    }
    @GetMapping ("/patient/update")
    public String showUpdatePatientForm(@PathVariable("fullname") String fullname, Patient patient, Model model){
        model.addAttribute("patient", patient);
        log.info("The display of the updatePatient page of a patient is functional");
        return PATIENT_UPDATE;
    }
}
