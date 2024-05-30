package com.medilabo.medilabo.services;

import com.medilabo.medilabo.model.Patient;

import java.util.Optional;

public interface IPatientService {

    public Iterable<Patient>  getPatientList();

    public Optional<Patient> getPatientByName(String name);

    public Patient savePatient(Patient patient);
}
