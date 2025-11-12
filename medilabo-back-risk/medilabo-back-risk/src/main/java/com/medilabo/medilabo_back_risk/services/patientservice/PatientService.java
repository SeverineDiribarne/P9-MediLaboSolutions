package com.medilabo.medilabo_back_risk.services.patientservice;

import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.medilabo.medilabo_back_risk.dto.PatientDTO;
import com.medilabo.medilabo_back_risk.model.Gender;

@Service
public class PatientService implements IPatientService {

    private Gender parseGender(String value) {
        if (value == null)
            return null;
        String genderValue = value.trim().toUpperCase(Locale.ROOT);
        // prises en charge simples
        if ("M".equals(genderValue) || "MALE".equals(genderValue) || "HOMME".equals(genderValue))
            return Gender.M;
        if ("F".equals(genderValue) || "FEMALE".equals(genderValue) || "FEMME".equals(genderValue))
            return Gender.F;
        if ("X".equals(genderValue) || "OTHER".equals(genderValue) || "AUTRE".equals(genderValue))
            return Gender.X;
        // fallback sécurisé
        try {
            return Gender.valueOf(genderValue);
        } catch (IllegalArgumentException ex) {
            return Gender.M;
        }
    }

    @Override
    public PatientDTO processPatientDTOData(Map<String, Object> patient) {
        if (patient == null || patient.isEmpty()) {
            // Données patient manquantes: lever une exception claire côté service
            throw new IllegalArgumentException("Les données du patient sont nulles ou vides (patient map)");
        }
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setBirthdate((String) patient.get("birthdate"));
        patientDTO.setAge(patientDTO.getPatientAge(patientDTO));
        patientDTO.setGender(parseGender((String) patient.get("gender")));

        return patientDTO;
    }
}