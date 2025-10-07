package com.medilabo.medilabo_back_mongo.services;

import java.util.List;

import com.medilabo.medilabo_back_mongo.model.Note;

public interface INoteService {

    List<Note> getNotes(String patientId);

    Note addNote(String patientId, String patientLastname, String note);

}
