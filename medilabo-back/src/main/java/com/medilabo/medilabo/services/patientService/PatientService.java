package com.medilabo.medilabo.services.patientService;

import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.repositories.IPatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.lang.Long.parseLong;

@Service
public class PatientService implements IPatientService {

    @Autowired
    IPatientRepository patientRepository;


    @Override
    public Iterable<Patient> getPatientList() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientById(long id) {//byID
        return patientRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }
}
