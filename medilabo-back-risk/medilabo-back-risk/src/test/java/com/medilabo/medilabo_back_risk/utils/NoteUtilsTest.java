package com.medilabo.medilabo_back_risk.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NoteUtilsTest {

    @Test
    @DisplayName("normalize supprime accents et met en minuscules")
    void normalizeWorks() {
        NoteUtils utils = new NoteUtils();
        assertEquals("hemoglobine a1c", utils.normalize("Hémoglobine A1C"));
        assertEquals("poids", utils.normalize("Poïds"));
    }

    @Test
    @DisplayName("mapConceptCounts compte chaque concept une fois malgré plusieurs synonymes")
    void mapCountsConcepts() {
        NoteUtils utils = new NoteUtils();
        String text = "HÉMOGLOBINE A1C élevée. Patiente fumeur fumeuse qui dit avoir essayé de fumer moins. Poids stable. Vertiges et vertige présents.";
        Map<String,Integer> map = utils.mapConceptCounts(text);
        // Concepts attendus : hémoglobine A1C, fumeur, poids, vertige => 4
        assertEquals(1, map.get("hémoglobine A1C"));
        assertEquals(1, map.get("fumeur")); // malgré fumeur/fumeuse/fumer
        assertEquals(1, map.get("poids"));
        assertEquals(1, map.get("vertige")); // vertiges + vertige
        // Absents
        assertEquals(0, map.get("microalbumine"));
        assertEquals(0, map.get("anormal"));
    }

    @Test
    @DisplayName("getNormalizedTriggerWords (canoniques) contient les noms normalisés des concepts")
    void normalizedConceptNames() {
        NoteUtils utils = new NoteUtils();
        List<String> normalized = utils.getNormalizedTriggerWords();
        assertTrue(normalized.contains("hemoglobine a1c"));
        assertTrue(normalized.contains("poids"));
        assertTrue(normalized.contains("fumeur"));
        assertTrue(normalized.contains("vertige"));
    }

    @Test
    @DisplayName("getTriggerWords retourne les concepts (11) dont 'hémoglobine A1C'")
    void getTriggerWords_hasConcepts() {
        NoteUtils utils = new NoteUtils();
        List<String> words = utils.getTriggerWords();
        assertEquals(11, words.size());
        assertTrue(words.contains("hémoglobine A1C"));
        assertTrue(words.contains("fumeur"));
        assertTrue(words.contains("vertige"));
    }

    @Test
    @DisplayName("normalize gère null en renvoyant une chaîne vide")
    void normalize_handlesNull() {
        NoteUtils utils = new NoteUtils();
        assertEquals("", utils.normalize(null));
    }
}
