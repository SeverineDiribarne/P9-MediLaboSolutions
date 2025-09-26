package com.medilabo.medilabo_back_mongo.services;

import java.util.List;

import com.medilabo.medilabo_back_mongo.model.Note;

public interface INoteService {

     public List<Note> getNotes(String patientId);

   public Note addNote(String patientId, String patientLastname, String note);
}
