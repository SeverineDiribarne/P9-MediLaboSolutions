package com.medilabo.medilabo_back_mongo.repositories;

import com.medilabo.medilabo_back_mongo.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INoteRepository extends MongoRepository<Note, String> {
    
}
