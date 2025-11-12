package com.medilabo.medilabo_back_risk.services.notesservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.medilabo.medilabo_back_risk.dto.NotesDTO;
import com.medilabo.medilabo_back_risk.model.Note;
import com.medilabo.medilabo_back_risk.utils.NoteUtils;

class NotesServiceTest {

    @Test
    @DisplayName("processNotesDTOData retourne 0 si DTO ou liste de notes nulle/vide")
    void returnsZeroWhenNoNotes() {
        NotesService service = new NotesService();
        service.noteUtils = new NoteUtils(); // champ package-private accessible dans le même package

        assertEquals(0, service.processNotesDTOData(null));
        assertEquals(0, service.processNotesDTOData(new NotesDTO(null)));
        assertEquals(0, service.processNotesDTOData(new NotesDTO(List.of())));
    }

    @Test
    @DisplayName("processNotesDTOData compte chaque mot déclencheur au plus une fois sur l'ensemble des notes")
    void countsUniqueTriggerWordsAcrossNotes() {
        NotesService service = new NotesService();
        service.noteUtils = new NoteUtils();

        // Prépare 3 notes dont 2 contiennent des déclencheurs et avec des doublons
        Note n1 = new Note(null, null, null,
                "Le patient présente une hémoglobine A1C élevée et un poids stable.");
        Note n2 = new Note(null, null, null,
                "Ancien fumeur, rechute récente. Poids en baisse.");
        Note n3 = new Note(null, null, null,
                "Aucun signe particulier.");

        NotesDTO dto = new NotesDTO(Arrays.asList(n1, n2, n3));

        // Déclencheurs attendus (normalisés par le service): hémoglobine A1C, poids, fumeur, rechute => 4
        int result = service.processNotesDTOData(dto);
        assertEquals(4, result);
    }
}
