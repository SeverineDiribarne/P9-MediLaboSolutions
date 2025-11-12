package com.medilabo.medilabo_back_risk.services.patientservice;

import java.util.Map;

import com.medilabo.medilabo_back_risk.dto.PatientDTO;

public interface IPatientService {

    PatientDTO processPatientDTOData(Map<String,Object> patient);

}
