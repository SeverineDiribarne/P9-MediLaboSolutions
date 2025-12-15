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
	// 1) Retrieve concepts and their normalized synonyms
	var normalizedConceptSynonyms = noteUtils.getNormalizedConceptSynonyms();

	// 2) Create a list of normalized notes and fill it with normalized content
		List<Note> normalizedNotes = new ArrayList<>();

		for (Note note : notesDTO.getNotes()) {
			if (note == null) continue;
			note.normalizeContent(noteUtils);
			normalizedNotes.add(note);
		}

		// Set of detected CONCEPTS (counted at most once globally)
		Set<String> foundConcepts = new HashSet<>();

		// 3) For each concept, check if at least one synonym appears in at least one note
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
		// return the number of concepts found
		return foundConcepts.size();
	}
}