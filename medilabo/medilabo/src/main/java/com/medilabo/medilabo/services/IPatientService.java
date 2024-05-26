package com.medilabo.medilabo.services;

import com.medilabo.medilabo.model.Patient;

import java.util.Optional;

public interface IPatientService {

    public Iterable<Patient>  getPatientsList();

    public Optional<Patient> getPatientListById(Integer id);

    public Patient savePatientList(Patient patient);
}
