package com.medilabo.medilabo_back_mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.medilabo_back_mongo.model.User;

@Repository
public interface IUserRepository extends MongoRepository<User, String> {
    
}
