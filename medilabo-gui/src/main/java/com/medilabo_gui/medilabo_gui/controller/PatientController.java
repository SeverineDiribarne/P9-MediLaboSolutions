package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.model.DiabetesRisk;
import com.medilabo_gui.medilabo_gui.model.Gender;
import com.medilabo_gui.medilabo_gui.model.Patient;
import com.medilabo_gui.medilabo_gui.model.NoteForm;
import com.medilabo_gui.medilabo_gui.services.patientservice.IPatientService;
import com.medilabo_gui.medilabo_gui.services.diabetesrisk.IDiabetesRisk;
import com.medilabo_gui.medilabo_gui.services.noteservice.INoteService;
import com.medilabo_gui.medilabo_gui.utils.JwtUtils;

import jakarta.validation.Valid;

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

@CrossOrigin
@Controller
@RequestMapping("/api/patient")
public class PatientController {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PatientController.class);
    private static final String LOG_ERROR = "The patient could not be validated" +
            " or registered in the database because the patient details were empty or" +
            " partially empty," +
            " with the exception of the address and telephone number, which are" +
            " optional.";
    private static final String LOGIN = "login";
    private static final String PATIENT_ADD = "add";
    private static final String PATIENT_UPDATE = "update";
    private static final String PATIENT_LIST = "list";
    private static final String PATIENT_DETAILS = "details";
    private static final String REDIRECT_PATIENT_DETAILS = "redirect:/api/patient/details/";
    private static final String REDIRECT_PATIENT_LIST = "redirect:/api/patient/list";

    @Autowired
    IPatientService patientService;

    @Autowired
    INoteService noteService;

    @Autowired
    IDiabetesRisk diabetesRiskService;

    @RequestMapping(value = "/list", method = { RequestMethod.POST, RequestMethod.GET })
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
            @Valid @ModelAttribute("patient") Patient newPatient,
            BindingResult result, Model model) {
        String jwtToken = (String) authentication.getDetails();
        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }
        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);
        // Validation standard
        if (result.hasErrors()) {
            logger.error(LOG_ERROR);
            model.addAttribute("errors", result.getAllErrors());
            return PATIENT_ADD;
        }
        // Validation métier complémentaire
        if (newPatient.getLastname() == null || newPatient.getLastname().isEmpty()) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgLastname", "Your lastname is empty");
            return PATIENT_ADD;
        }
        if (newPatient.getFirstname() == null || newPatient.getFirstname().isEmpty()) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgFirstname", "Your firstname is empty");
            return PATIENT_ADD;
        }
        if (newPatient.getBirthdate() == null || newPatient.getBirthdate().isEmpty()) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgBirthdate", "Your birthdate is empty");
            return PATIENT_ADD;
        }
        if (newPatient.getGender() == null || (newPatient.getGender() != Gender.M && newPatient.getGender() != Gender.F
                && newPatient.getGender() != Gender.X)) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgGender", "Your gender is incorrect");
            return PATIENT_ADD;
        }
        // Address and telephone are optional, but they can be standardized here though
        // need
        try {
            patientService.addPatient(newPatient, jwtToken);
            return REDIRECT_PATIENT_LIST;
        } catch (Exception ex) {
            logger.error("Exception during service addPatient", ex);
            model.addAttribute("addError", "Erreur lors de l'ajout du patient : " + ex.getMessage());
            return PATIENT_ADD;
        }
    }

    @GetMapping("/details/{id}")
    public String showPatientDetails(UsernamePasswordAuthenticationToken authentication,
            @PathVariable String id, Model model) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        // Recovery of patient information in the medilabo-back database

        Patient patient;
        try {
            // Call via the gateway (port 8090) to collect the patient by their id
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
        // Recovery of patient notes via the notes service

        try {
            var notes = noteService.getNotesByPatientId(patient.getPatientId(), jwtToken);
            model.addAttribute("notes", notes);
        } catch (Exception ex) {
            logger.error("Exception lors de la récupération des notes du patient {}", patient.getPatientId(), ex);
            model.addAttribute("notes", Collections.emptyList());
        }
        // Preparing the form object for adding a note (used by
        // th:object="${noteForm}")
        if (!model.containsAttribute("noteForm")) {
            com.medilabo_gui.medilabo_gui.model.NoteForm noteForm = new com.medilabo_gui.medilabo_gui.model.NoteForm();
            noteForm.setPatientId(patient.getPatientId());
            noteForm.setPatientLastname(patient.getLastname());
            model.addAttribute("noteForm", noteForm);
        }
        // Recovery of patient diabetes risk information in the medilabo-back-risk
        // database

        DiabetesRisk diabetesRisk = new DiabetesRisk();
        try {
            // Call via the gateway (port 8090) to recover the risk of diabetes
            // patient by his id
            ResponseEntity<DiabetesRisk> responsePatientDiabetesRisk = diabetesRiskService.getPatientDiabetesRisk(id,
                    jwtToken);
            if (responsePatientDiabetesRisk.getStatusCode().is2xxSuccessful()
                    && responsePatientDiabetesRisk.getBody() != null) {
                diabetesRisk = responsePatientDiabetesRisk.getBody();
                logger.info("Patient's risk of diabetes retrieved via gateway for id {}", id);
            } else {
                logger.warn("Gateway call succeeded but body empty or status not OK for id {}", id);
                diabetesRisk = new DiabetesRisk();
            }
        } catch (Exception ex) {
            logger.error("Exception during REST call to /api/risk/diabetes/" + id, ex);
            diabetesRisk = new DiabetesRisk();
        }
        // Space for message relating to the patient's diabetes status

        model.addAttribute("diabetesMessage", diabetesRisk.getDiabetesRiskLevel());

        return PATIENT_DETAILS;
    }

    @GetMapping("/update/{id}")
    public String showUpdatePatientPage(UsernamePasswordAuthenticationToken authentication,
            Model model, @PathVariable String id) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        ResponseEntity<Patient> patientToUpdate = patientService.getPatientToUpdateById(id, jwtToken);
        if (patientToUpdate.getStatusCode().is2xxSuccessful() && patientToUpdate.getBody() != null) {
            Patient newPatient = patientToUpdate.getBody();
            logger.info(newPatient.toString());
            model.addAttribute("patient", newPatient);
        } else {
            logger.warn("Gateway call succeeded but body empty or status not OK for id {}", id);
            model.addAttribute("patient", new Patient());
        }
        logger.info("The display of the updatePatient page of a patient is functional");
        return PATIENT_UPDATE;
    }

    @PostMapping("/update/{id}")
    public String updatePatient(UsernamePasswordAuthenticationToken authentication,
            @PathVariable String id,
            @Valid @ModelAttribute("patient") Patient patient,
            BindingResult result, Model model) {

        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        if (result.hasErrors()) {
            logger.error(LOG_ERROR);
            model.addAttribute("errors", result.getAllErrors());
            return PATIENT_UPDATE;
        }

        if (patient.getLastname().isEmpty()) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgLastname", "Your lastname is empty");
            return PATIENT_UPDATE;
        }

        if (patient.getFirstname().isEmpty()) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgFirstname", "Your firstname is empty");
            return PATIENT_UPDATE;
        }

        if (patient.getBirthdate().isEmpty()) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgBirthdate", "Your birthdate is empty");
            return PATIENT_UPDATE;
        }

        if (patient.getGender() != Gender.M && patient.getGender() != Gender.F
                && patient.getGender() != Gender.X) {
            logger.error(LOG_ERROR);
            model.addAttribute("msgGender", "Your gender is incorrect");
            return PATIENT_UPDATE;
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        // Appel du service d'update
        ResponseEntity<Patient> updateResponse = patientService.updatePatient(id, patient, jwtToken);
        if (updateResponse.getStatusCode().is2xxSuccessful() && updateResponse.getBody() != null) {
            logger.info("Patient updated successfully : {}", updateResponse.getBody().getPatientId());
            return "redirect:/api/patient/list";
        } else {
            logger.error("Update failed for patient id {} with status {}", id, updateResponse.getStatusCode());
            model.addAttribute("patient", patient);
            model.addAttribute("updateError", "La mise à jour a échoué");
            return PATIENT_UPDATE;
        }
    }

    @PostMapping("/notes")
    public String addNote(UsernamePasswordAuthenticationToken authentication,
            @Valid @ModelAttribute("noteForm") NoteForm noteForm,
            BindingResult result,
            Model model) {
        String jwtToken = (String) authentication.getDetails();
        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return LOGIN;
        }

        if (result.hasErrors()) {
            // Recharge patient and notes for redisplay form with errors
            try {
                ResponseEntity<Patient> responsePatientDetails = patientService
                        .getPatientDetails(String.valueOf(noteForm.getPatientId()), jwtToken);
                if (responsePatientDetails.getStatusCode().is2xxSuccessful()
                        && responsePatientDetails.getBody() != null) {
                    model.addAttribute("patient", responsePatientDetails.getBody());
                } else {
                    model.addAttribute("patient", new Patient());
                }
            } catch (Exception e) {
                logger.error("Erreur récupération patient lors erreur validation note", e);
                model.addAttribute("patient", new Patient());
            }
            try {
                var notes = noteService.getNotesByPatientId(noteForm.getPatientId(), jwtToken);
                model.addAttribute("notes", notes);
            } catch (Exception ex) {
                logger.error("Erreur récupération notes lors erreur validation note", ex);
                model.addAttribute("notes", Collections.emptyList());
            }
            return PATIENT_DETAILS;
        }

        try {
            noteService.addNote(noteForm, jwtToken);
            logger.info("Note ajoutée pour patient {}", noteForm.getPatientId());
        } catch (Exception ex) {
            logger.error("Erreur lors de l'ajout d'une note pour patient {}", noteForm.getPatientId(), ex);
            model.addAttribute("noteError", "Erreur lors de l'ajout de la note");
        }
        return REDIRECT_PATIENT_DETAILS + noteForm.getPatientId();
    }

    @PostMapping("/notes/delete/{noteId}")
    public String deleteNote(UsernamePasswordAuthenticationToken authentication,
            @PathVariable String noteId,
            @RequestParam Long patientId) {
        String jwtToken = (String) authentication.getDetails();
        if (jwtToken == null || jwtToken.isEmpty()) {
            return LOGIN;
        }
        try {
            noteService.deleteNote(noteId, jwtToken);
            logger.info("Note {} supprimée", noteId);
        } catch (Exception ex) {
            logger.error("Erreur lors de la suppression de la note {}", noteId, ex);
        }
        return REDIRECT_PATIENT_DETAILS + patientId;
    }

    @PostMapping("/notes/edit/{noteId}")
    public String editNote(UsernamePasswordAuthenticationToken authentication,
            @PathVariable String noteId,
            @RequestParam Long patientId,
            @RequestParam String noteContent) {
        String jwtToken = (String) authentication.getDetails();
        if (jwtToken == null || jwtToken.isEmpty()) {
            return LOGIN;
        }
        try {
            noteService.updateNote(noteId, noteContent, jwtToken);
            logger.info("Note {} modifiée", noteId);
        } catch (Exception ex) {
            logger.error("Erreur lors de la modification de la note {}", noteId, ex);
        }
        return REDIRECT_PATIENT_DETAILS + patientId;
    }
}
