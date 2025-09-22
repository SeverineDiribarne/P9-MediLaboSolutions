package com.medilabo_gui.medilabo_gui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo_gui.medilabo_gui.dto.UserPublicDTO;
import com.medilabo_gui.medilabo_gui.model.Patient;
import com.medilabo_gui.medilabo_gui.services.IPatientService;
import com.medilabo_gui.medilabo_gui.utils.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private static final String PATIENT_ADD = "add";
    private static final String PATIENT_UPDATE = "update";
    private static final String REDIRECT_PATIENT_LIST = "redirect:/list";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    IPatientService patientService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public String showPatientsList(UsernamePasswordAuthenticationToken authentication, Model model) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return "login";
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        HttpHeaders headersRequest = new HttpHeaders();
        headersRequest.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersRequest.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headersRequest);

        List<Patient> patients = Collections.emptyList();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://localhost:8090/api/patient/list",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<String>() {
                    });
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                patients = mapper.readValue(
                        response.getBody(),
                        new TypeReference<List<Patient>>() {
                        });
            }
        } catch (Exception ex) {
            logger.error("Exception during REST call to /api/patient/list", ex);
            patients = Collections.emptyList();
        }
        model.addAttribute("patients", patients);
        return "list";
    }

    @GetMapping("/add")
    public String showAddPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        logger.info("The display of the addPatient page of a patient is functional");
        return PATIENT_ADD;
    }

    @PostMapping("/addvalidate")
    public String addPatient(UsernamePasswordAuthenticationToken authentication,
            @ModelAttribute("patient") Patient newPatient, BindingResult result, Model model) {
        String jwtToken = (String) authentication.getDetails();

        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return "login";
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        HttpHeaders headersRequest = new HttpHeaders();
        headersRequest.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersRequest.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headersRequest);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://localhost:8090/api/patient/addvalidate",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<String>() {
                    });
            // if (response.getStatusCode() == HttpStatus.OK) {
            // ObjectMapper mapper = new ObjectMapper();
            // patients = mapper.readValue(
            // response.getBody(),
            // new TypeReference<List<Patient>>() {
            // });
            // }
            // } catch (Exception ex) {
            // logger.error("Exception during REST call to /api/patient/list", ex);
            // patients = Collections.emptyList();
            // }
            // model.addAttribute("patients", patients);
            return "list";
        } catch (Exception ex) {
            logger.error("Exception during REST call to /api/patient/addvalidate", ex);
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
    public String showPatientDetails(@PathVariable long id, Model model) {
        ResponseEntity<Patient> patient = patientService.getPatientDetails(id);
        model.addAttribute("patient", patient.getBody());
        logger.info(" patient is found and returned to view");
        return "details";
    }

    // TODO : A revoir cette methode du front vers le back
    @GetMapping("/update/{id}")
    public String showUpdatePatientForm(@PathVariable long id, Model model) {
        ResponseEntity<Patient> patientToUpdate = patientService.getPatientToUpdate(id);
        model.addAttribute("patient", patientToUpdate);
        logger.info("The display of the updatePatient page of a patient is functional");
        return PATIENT_UPDATE;
    }
}
