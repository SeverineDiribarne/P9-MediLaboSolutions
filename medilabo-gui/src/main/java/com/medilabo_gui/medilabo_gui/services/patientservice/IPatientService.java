package com.medilabo_gui.medilabo_gui.services.patientservice;

import com.medilabo_gui.medilabo_gui.model.Patient;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPatientService {

    ResponseEntity<List<Patient>> getPatientList();

    ResponseEntity<Patient> getPatientDetails(long id);

    ResponseEntity<Patient> getPatientToUpdate(long id);

    ResponseEntity<Patient> addPatient(Patient patient);
}
