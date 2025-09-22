package com.medilabo.medilabo_back_mongo.services;

import java.util.Optional;

import com.medilabo.medilabo_back_mongo.model.Note;

public interface INoteService {

    Iterable<Note> getNoteList();

    Optional<Note> getNoteById(long id);
}
