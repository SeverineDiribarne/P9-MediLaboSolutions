package com.medilabo.medilabo_back_mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "note")
public class Note {

    @Id
    private String id;
    private String patientId;
    @Field("patient")
    private String patientLastname;
    private String note;


}
