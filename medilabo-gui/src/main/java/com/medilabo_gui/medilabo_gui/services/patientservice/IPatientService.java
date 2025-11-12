package com.medilabo_gui.medilabo_gui.services.patientservice;

import com.medilabo_gui.medilabo_gui.model.Patient;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPatientService {

    ResponseEntity<List<Patient>> getPatientList(String jwtToken);

    ResponseEntity<Patient> addPatient(Patient patient, String jwtToken);

    ResponseEntity<Patient> getPatientDetails(String id, String jwtToken);

    ResponseEntity<Patient> getPatientToUpdateById(String id, String jwtToken);
    ResponseEntity<Patient> updatePatient(String id, Patient patient, String jwtToken);
   
}
