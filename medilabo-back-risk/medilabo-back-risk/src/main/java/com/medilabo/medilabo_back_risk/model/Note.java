package com.medilabo.medilabo_back_risk.model;


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
     * Normalizes and updates this note's content using the provided utility,
     * without exposing content getters/setters to the caller side.
     */
    public void normalizeContent(NoteUtils utils) {
        if (utils == null) return;
        this.note = utils.normalize(this.note);
    }

    /**
     * Returns the textual content of the note (after possible normalization).
     * Keeping an alias named "content" avoids any ambiguity on the caller side.
     */
    public String getContent() {
        return this.note;
    }
}
