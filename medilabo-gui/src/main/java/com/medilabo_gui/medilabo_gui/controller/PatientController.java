package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.model.Patient;
import com.medilabo_gui.medilabo_gui.services.patientservice.IPatientService;
import com.medilabo_gui.medilabo_gui.services.noteservice.INoteService;
import com.medilabo_gui.medilabo_gui.utils.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "https://localhost:8090")
@Controller
@RequestMapping("/api/patient")
public class PatientController {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PatientController.class);
    // private static final String LOG_ERROR = "The patient could not be validated
    // or registered in the database because the patient details were empty or
    // partially empty," +
    // " with the exception of the address and telephone number, which are
    // optional.";
    private static final String LOGIN = "login";
    private static final String PATIENT_ADD = "add";
    private static final String PATIENT_UPDATE = "update";
    private static final String PATIENT_LIST = "list";
    private static final String PATIENT_DETAILS = "details";

    @Autowired
    IPatientService patientService;

    @Autowired
    INoteService noteService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public String showPatientsList(UsernamePasswordAuthenticationToken authentication, Model model) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        List<Patient> patients = Collections.emptyList();
        try {
            ResponseEntity<List<Patient>> response = patientService.getPatientList(jwtToken);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                patients = response.getBody();
            }
        } catch (Exception ex) {
            logger.error("Exception during REST call to /api/patient/list", ex);
            patients = Collections.emptyList();
        }
        model.addAttribute("patients", patients);
        return PATIENT_LIST;
    }

    @GetMapping("/add")
    public String showAddPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        logger.info("The display of the addPatient page of a patient is functional");
        return PATIENT_ADD;
    }

    @PostMapping("/addpatient")
    public String addPatient(UsernamePasswordAuthenticationToken authentication,
            @ModelAttribute("patient") Patient newPatient, BindingResult result, Model model) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);
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
        try {
            // Appel service pour l'ajout
            patientService.addPatient(newPatient, jwtToken);

            // Récupération liste mise à jour via service
            List<Patient> patients = Collections.emptyList();
            try {
                ResponseEntity<List<Patient>> listResponse = patientService.getPatientList(jwtToken);
                if (listResponse.getStatusCode().is2xxSuccessful() && listResponse.getBody() != null) {
                    patients = listResponse.getBody();
                }
            } catch (Exception e) {
                logger.error("Exception during list refresh after addPatient", e);
            }
            model.addAttribute("patients", patients);
            return PATIENT_LIST;
        } catch (Exception ex) {
            logger.error("Exception during service addPatient", ex);
            return PATIENT_ADD;
        }
        // System.out.println(patient.getLastname());
        // if (result.hasErrors()) {
        // model.addAttribute("errors", result.getAllErrors());
        // return PATIENT_ADD;
        // }
        // patientService.addPatient(patient);
        // return REDIRECT_PATIENT_LIST;
    }

    @GetMapping("/details/{id}")
    public String showPatientDetails(UsernamePasswordAuthenticationToken authentication,
            @PathVariable long id, Model model) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        // Recuperation des informations patient dans la base de donnees medilabo-back
        Patient patient;
        try {
            // Appel via la gateway (port 8090) pour récupérer le patient par son id
            ResponseEntity<Patient> responsePatientDetails = patientService.getPatientDetails(id, jwtToken);
            if (responsePatientDetails.getStatusCode().is2xxSuccessful() && responsePatientDetails.getBody() != null) {
                patient = responsePatientDetails.getBody();
                logger.info("Patient details retrieved via gateway for id {}", id);
            } else {
                logger.warn("Gateway call succeeded but body empty or status not OK for id {}", id);
                patient = new Patient();
            }
        } catch (Exception ex) {
            logger.error("Exception during REST call to /api/patient/details/" + id, ex);
            patient = new Patient();
        }
        model.addAttribute("patient", patient);
        // Récupération des notes du patient via le service notes
        try {
            var notes = noteService.getNotesByPatient(patient.getPatientId(), jwtToken);
            model.addAttribute("notes", notes);
        } catch (Exception ex) {
            logger.error("Exception lors de la récupération des notes du patient {}", patient.getPatientId(), ex);
            model.addAttribute("notes", Collections.emptyList());
        }
        // Préparation de l'objet formulaire pour l'ajout d'une note (utilisé par th:object="${noteForm}")
        if (!model.containsAttribute("noteForm")) {
            com.medilabo_gui.medilabo_gui.model.NoteForm noteForm = new com.medilabo_gui.medilabo_gui.model.NoteForm();
            noteForm.setPatientId(patient.getPatientId());
            noteForm.setPatientLastname(patient.getLastname());
            model.addAttribute("noteForm", noteForm);
        }
        return PATIENT_DETAILS;
    }

    // TODO : A revoir cette methode du front vers le back
    @GetMapping("/update/{id}")
    public String showUpdatePatientForm(UsernamePasswordAuthenticationToken authentication,
            @PathVariable long id, Model model) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        ResponseEntity<Patient> patientToUpdate = patientService.getPatientToUpdate(id, jwtToken);
        model.addAttribute("patient", patientToUpdate);
        logger.info("The display of the updatePatient page of a patient is functional");
        return PATIENT_UPDATE;
    }
}
