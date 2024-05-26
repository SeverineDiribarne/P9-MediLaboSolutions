package com.medilabo.medilabo.services;

import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.repositories.IPatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class PatientService implements IPatientService{

    @Autowired
    IPatientRepository patientRepository;


    public Iterable<Patient>  getPatientsList(){
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientListById(Integer id) {
        return patientRepository.findById(Long.valueOf(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public Patient savePatientList(Patient patient) {
        return patientRepository.save(patient);
    }
}
