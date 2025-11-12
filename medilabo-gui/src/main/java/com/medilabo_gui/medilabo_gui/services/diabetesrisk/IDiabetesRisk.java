package com.medilabo_gui.medilabo_gui.services.diabetesrisk;

import com.medilabo_gui.medilabo_gui.model.DiabetesRisk;
import org.springframework.http.ResponseEntity;

public interface IDiabetesRisk {

   ResponseEntity<DiabetesRisk> getPatientDiabetesRisk(String id, String jwtToken);
}
