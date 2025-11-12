package com.medilabo.medilabo_back_risk.model;

import java.util.List;

import com.medilabo.medilabo_back_risk.utils.NoteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    
    private String id;
    private String patientId;
    private String patientLastname;
    private String note;

    /**
     * Normalise et met à jour le contenu de cette note via l'utilitaire fourni,
     * sans exposer get/set du contenu côté appelant.
     */
    public void normalizeContent(NoteUtils utils) {
        if (utils == null) return;
        this.note = utils.normalize(this.note);
    }

    /**
     * Retourne le contenu textuel de la note (après éventuelle normalisation).
     * Garder un alias nommé "content" évite toute ambiguïté côté appelant.
     */
    public String getContent() {
        return this.note;
    }
}
