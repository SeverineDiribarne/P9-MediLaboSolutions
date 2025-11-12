package com.medilabo.medilabo_back_risk.services.notesservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medilabo.medilabo_back_risk.dto.NotesDTO;
import com.medilabo.medilabo_back_risk.model.Note;
import com.medilabo.medilabo_back_risk.utils.NoteUtils;

@Service
public class NotesService implements INotesService {

	@Autowired 
	NoteUtils noteUtils;	

	
	public int processNotesDTOData(NotesDTO notesDTO) {

		if (notesDTO == null || notesDTO.getNotes() == null) {
			return 0;
		}
	//1) récupérer les concepts et leurs synonymes normalisés
	var normalizedConceptSynonyms = noteUtils.getNormalizedConceptSynonyms();

	// 2) créer une liste de notes normalisées et la remplir avec les notes normalisées
		List<Note> normalizedNotes = new ArrayList<>();

		for (Note note : notesDTO.getNotes()) {
			if (note == null) continue;
			note.normalizeContent(noteUtils);
			normalizedNotes.add(note);
		}

		// Ensemble des CONCEPTS détectés (comptés au plus une fois globalement)
		Set<String> foundConcepts = new HashSet<>();

		// 3) Pour chaque concept, vérifier si au moins un de ses synonymes apparaît dans au moins une note
		for (var entry : normalizedConceptSynonyms.entrySet()) {
			String concept = entry.getKey();
			List<String> synonyms = entry.getValue();
			boolean present = false;
			for (Note note : normalizedNotes) {
				String content = note.getContent();
				for (String syn : synonyms) {
					if (content.contains(syn)) {
						present = true;
						break;
					}
				}
				if (present) break;
			}
			if (present) {
				foundConcepts.add(concept);
			}
		}
		// renvoyer le nombre de concepts trouvés
		return foundConcepts.size();
	}
}