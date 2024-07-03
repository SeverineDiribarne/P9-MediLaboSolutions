package com.medilabo.medilabo.services;

import com.medilabo.medilabo.model.Patient;

import java.util.Optional;

public interface IPatientService {

    public Iterable<Patient>  getPatientList();

    public Optional<Patient> getPatientById(long id);

    public Patient savePatient(Patient patient);
}
