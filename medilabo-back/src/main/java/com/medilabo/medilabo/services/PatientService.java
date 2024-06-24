package com.medilabo.medilabo.services;

import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.repositories.IPatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService implements IPatientService{

    @Autowired
    IPatientRepository patientRepository;


    @Override
    public Iterable<Patient> getPatientList() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientByFullname(String fullname) {//byFullName
      String[] name = fullname.split(" ");
      String lastname = name[0];
      String firstname = name[1];
        return patientRepository.findByLastnameAndFirstname(lastname, firstname);
    }

    @Transactional(rollbackFor = Exception.class)
    public Patient savePatient(Patient patient) {

        return patientRepository.save(patient);
    }
}
