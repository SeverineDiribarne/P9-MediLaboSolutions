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
    public Optional<Patient> getPatientByName(String name) { //byName
        return patientRepository.findByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public Patient savePatient(Patient patient) {

        return patientRepository.save(patient);
    }
}
