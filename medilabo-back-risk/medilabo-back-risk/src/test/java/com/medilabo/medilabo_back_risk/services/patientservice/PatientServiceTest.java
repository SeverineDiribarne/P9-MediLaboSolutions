package com.medilabo.medilabo_back_risk.services.patientservice;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.medilabo.medilabo_back_risk.dto.PatientDTO;
import com.medilabo.medilabo_back_risk.model.Gender;
import com.medilabo.medilabo_back_risk.utils.AgeUtils;

class PatientServiceTest {

    @Test
    @DisplayName("processPatientDTOData mappe les champs et calcule l'âge")
    void processPatient_ok() {
        PatientService svc = new PatientService();
        Map<String,Object> patient = new HashMap<>();
        patient.put("birthdate", "1980-01-01");
        patient.put("gender", "HOMME");

        PatientDTO dto = svc.processPatientDTOData(patient);
        assertEquals(AgeUtils.computeAge("1980-01-01"), dto.getAge());
        assertEquals(Gender.M, dto.getGender());
    }

    @Test
    @DisplayName("processPatientDTOData accepte diverses valeurs de genre")
    void processPatient_genderVariants() {
        PatientService svc = new PatientService();
        Map<String,Object> p = new HashMap<>();
        p.put("birthdate", "1980-01-01");

        p.put("gender", "F");
        assertEquals(Gender.F, svc.processPatientDTOData(p).getGender());

        p.put("gender", "femme");
        assertEquals(Gender.F, svc.processPatientDTOData(p).getGender());

        p.put("gender", "other");
        assertEquals(Gender.X, svc.processPatientDTOData(p).getGender());

        p.put("gender", "UNKNOWN"); // fallback -> Gender.M (implémentation courante)
        assertEquals(Gender.M, svc.processPatientDTOData(p).getGender());

        p.put("gender", "MALE");
        assertEquals(Gender.M, svc.processPatientDTOData(p).getGender());

        p.put("gender", "HOMME");
        assertEquals(Gender.M, svc.processPatientDTOData(p).getGender());

        p.put("gender", "AUTRE");
        assertEquals(Gender.X, svc.processPatientDTOData(p).getGender());

        p.put("gender", "X");
        assertEquals(Gender.X, svc.processPatientDTOData(p).getGender());

        p.remove("gender"); // null -> parseGender retourne null
        assertNull(svc.processPatientDTOData(p).getGender());
    }

    @Test
    @DisplayName("processPatientDTOData lève IllegalArgumentException pour null/empty")
    void processPatient_invalid() {
        PatientService svc = new PatientService();
        assertThrows(IllegalArgumentException.class, () -> svc.processPatientDTOData(null));
        assertThrows(IllegalArgumentException.class, () -> svc.processPatientDTOData(Map.of()));
    }
}
