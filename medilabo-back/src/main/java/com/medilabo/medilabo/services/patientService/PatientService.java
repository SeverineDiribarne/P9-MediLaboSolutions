package com.medilabo.medilabo.services.patientService;

import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.repositories.IPatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PatientService implements IPatientService {

    @Autowired
    IPatientRepository patientRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Iterable<Patient> getPatientList() {
        return patientRepository.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Patient> getPatientById(long id) {// byID
        return patientRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Patient updatePatient(Long id, Patient updatedPatient) {
        Optional<Patient> existingOpt = patientRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Patient not found");
        }
        Patient existing = existingOpt.get();
        // Utilise le constructeur pour créer le patient mis à jour (id conservé)
        Patient patientToSave = new Patient(
                existing.getPatientId(),
                updatedPatient.getLastname(),
                updatedPatient.getFirstname(),
                updatedPatient.getBirthdate(),
                updatedPatient.getGender(),
                updatedPatient.getAddress(),
                updatedPatient.getPhoneNumber());
        return patientRepository.save(patientToSave);
    }
}
