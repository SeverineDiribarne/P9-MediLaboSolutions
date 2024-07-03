package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.model.Patient;
import com.medilabo_gui.medilabo_gui.services.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api")
public class PatientController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PatientController.class);
  //  private static final String LOG_ERROR = "The patient could not be validated or registered in the database because the patient details were empty or partially empty," +
   //         " with the exception of the address and telephone number, which are optional.";
    private static final String PATIENT_ADD = "add";
    private static final String PATIENT_UPDATE = "update";
  //  private static final String REDIRECT_PATIENT_LIST = "redirect:/list";

    @Autowired
    IPatientService patientService;

    @GetMapping("/patient/list")
    public String listPatients(Model model) {
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

    @GetMapping("/patient/details/{id}")
    public String showPatientDetails(@PathVariable long id, Model model){
        ResponseEntity<Patient> patient = patientService.getPatientDetails(id);
        System.out.println(patient.getBody());
        model.addAttribute("patient", patient.getBody());
        log.info(" patient is found and returned to view");
        return "details";
    }

    @GetMapping ("/patient/update")
    public String showUpdatePatientForm(@RequestParam("lastname") String lastname, @RequestParam ("firstname") String firstname, Patient patient, Model model){
        model.addAttribute("patient", patient);
        log.info("The display of the updatePatient page of a patient is functional");
        return PATIENT_UPDATE;
    }
    @PostMapping("/savePatient")
    public String SavePatient (){
        ResponseEntity<List<Patient>> patients = patientService.getPatientList(); // a retravailler
        return "list";
    }
}
