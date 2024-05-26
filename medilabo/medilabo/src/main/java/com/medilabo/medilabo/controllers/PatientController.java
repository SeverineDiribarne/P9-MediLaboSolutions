package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatientController {

    @Autowired
    PatientService patientService;

    @RequestMapping("/patient/list")
    public String home (Model model){


        return "patient/list";
    }

    @GetMapping ("/patient/add")
    public String addPatientInformation (Model model){


    return "patient/add";
    }

    @PostMapping("/patient/update/{name}")
    public String updatePatientInformation(@PathVariable("name") String name, Model model){


        return "patient/update";
    }
}
