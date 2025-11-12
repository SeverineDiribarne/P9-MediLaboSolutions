package com.medilabo.medilabo_back_risk.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.medilabo.medilabo_back_risk.utils.NoteUtils;

class NoteTest {

    @Test
    @DisplayName("normalizeContent met à jour la note en normalisé")
    void normalizeAndGetContent() {
        NoteUtils utils = new NoteUtils();
        Note n = new Note(null, null, null, "Hémoglobine A1C");
        n.normalizeContent(utils);
        assertEquals("hemoglobine a1c", n.getContent());
    }

    @Test
    @DisplayName("getContent retourne le texte brut quand non normalisé")
    void getContentRaw() {
        Note n = new Note(null, null, null, "Texte Brut");
        assertEquals("Texte Brut", n.getContent());
    }
}
