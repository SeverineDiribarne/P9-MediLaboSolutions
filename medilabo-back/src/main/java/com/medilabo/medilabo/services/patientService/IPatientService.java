package com.medilabo.medilabo.services.patientService;

import com.medilabo.medilabo.model.Patient;

import java.util.Optional;

public interface IPatientService {

    public Iterable<Patient>  getPatientList();

    public Optional<Patient> getPatientById(long id);

    public Patient savePatient(Patient patient);

    /**
     * Met à jour un patient existant avec les champs du patient passé en paramètre (hors id).
     * @param id l'id du patient à mettre à jour
     * @param updatedPatient les nouvelles valeurs (hors id)
     * @return le patient mis à jour
     */
    public Patient updatePatient(Long id, Patient updatedPatient);
}
