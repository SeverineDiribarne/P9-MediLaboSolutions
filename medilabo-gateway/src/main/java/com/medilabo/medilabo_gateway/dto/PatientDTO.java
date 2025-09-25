package com.medilabo.medilabo_gateway.dto;

import com.medilabo.medilabo_gateway.models.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {


    private  long patientId;

    private String lastname ="";

    private String firstname = "";

    private String birthdate = "";

    private Gender gender = Gender.M;

    private String address = " ";

    private String phoneNumber =" ";

}
