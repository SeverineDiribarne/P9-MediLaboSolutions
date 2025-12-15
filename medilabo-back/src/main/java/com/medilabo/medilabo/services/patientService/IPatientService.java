package com.medilabo.medilabo.services.patientService;

import com.medilabo.medilabo.model.Patient;

import java.util.Optional;

public interface IPatientService {

    public Iterable<Patient>  getPatientList();

    public Optional<Patient> getPatientById(long id);

    public Patient savePatient(Patient patient);

    /** 
    * Updates an existing patient with the passed patient fields as a parameter (excluding id). 
    * @param id the patient id to update 
    * @param updatedPatient the new values (excluding id) 
    * @return the updated patient 
    */
    public Patient updatePatient(Long id, Patient updatedPatient);
}
