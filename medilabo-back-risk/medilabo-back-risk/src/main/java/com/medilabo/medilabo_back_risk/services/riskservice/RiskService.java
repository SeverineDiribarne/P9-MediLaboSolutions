package com.medilabo.medilabo_back_risk.services.riskservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medilabo.medilabo_back_risk.dto.PatientDTO;
import com.medilabo.medilabo_back_risk.services.notesservice.INotesService;
import com.medilabo.medilabo_back_risk.services.patientservice.IPatientService;

@Service
public class RiskService {

	@Autowired
	IPatientService patientService;

	@Autowired
	INotesService notesService;

	// Rules for processing the diabetes risk message

	public String processRiskData(PatientDTO patientDTO, int triggerWordCount) {
		// Implement the risk calculation logic here
		if (triggerWordCount <= 1) {
			return "NONE";
		}
		if (triggerWordCount >= 2 && triggerWordCount < 6 && patientDTO.getAge() > 30) {
			return "BORDERLINE";
		}
		if (patientDTO.getAge() < 30 && patientDTO.getGender().toString().equals("M") && triggerWordCount == 3) {
			return "IN_DANGER";
		}
		if (patientDTO.getAge() < 30 && patientDTO.getGender().toString().equals("F") && triggerWordCount == 4) {
			return "IN_DANGER";
		}
		if (patientDTO.getAge() >= 30 && triggerWordCount >= 6 && triggerWordCount < 8) {
			return "IN_DANGER";
		}
		if (patientDTO.getAge() < 30 && patientDTO.getGender().toString().equals("M") && triggerWordCount >= 5) {
			return "EARLY_ONSET";
		}
		if (patientDTO.getAge() < 30 && patientDTO.getGender().toString().equals("F") && triggerWordCount >= 7) {
			return "EARLY_ONSET";
		}
		if(patientDTO.getAge() >= 30 && triggerWordCount >= 8) {
			return "EARLY_ONSET";
		}
		else{
		return "UNDETERMINED";
		}
	}
}
