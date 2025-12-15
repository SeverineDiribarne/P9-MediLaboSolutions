package com.medilabo.medilabo_back_risk.services.riskservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.medilabo.medilabo_back_risk.dto.NotesDTO;
import com.medilabo.medilabo_back_risk.dto.PatientDTO;
import com.medilabo.medilabo_back_risk.model.Gender;
import com.medilabo.medilabo_back_risk.model.Note;
import com.medilabo.medilabo_back_risk.services.notesservice.NotesService;
import com.medilabo.medilabo_back_risk.utils.NoteUtils;

/**
 * Test simple bout-en-bout: comptage par concepts (synonymes) puis classification.
 */
class RiskEndToEndConceptsTest {

    @Test
    @DisplayName("Patiente <30 ans avec ≥7 concepts -> EARLY_ONSET (synonymes fumer/vertige inclus)")
    void femaleUnder30EarlyOnsetWithSynonyms() {
        // Prépare les notes: inclure des synonymes ("fumer") et des variations (singulier/pluriel)
        String content = String.join(" ",
                "Hémoglobine A1C élevée.",
                "Microalbumine détectée.",
                "Taille 170 cm.",
                "Poids anormal depuis 3 mois.",
                "Cholestérol augmenté.",
                "Patiente a commencé à fumer récemment.",
                "Sensation de vertige et parfois des vertiges.");

        NotesDTO notesDTO = new NotesDTO(List.of(new Note(null, null, null, content)));

        NotesService notesService = new NotesService();
        try {
            var field = NotesService.class.getDeclaredField("noteUtils");
            field.setAccessible(true);
            field.set(notesService, new NoteUtils());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int triggerCount = notesService.processNotesDTOData(notesDTO); // devrait être >=7

        PatientDTO patient = new PatientDTO();
        patient.setAge(23);
        patient.setGender(Gender.F);

        RiskService riskService = new RiskService();
        String level = riskService.processRiskData(patient, triggerCount);

        assertEquals("EARLY_ONSET", level);
    }
}
