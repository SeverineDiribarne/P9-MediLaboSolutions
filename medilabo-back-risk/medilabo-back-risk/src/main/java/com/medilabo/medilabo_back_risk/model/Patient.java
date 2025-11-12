package com.medilabo.medilabo_back_risk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Long id;
    private String lastname;
    private String firstname;
    private String birthdate;
    private Gender gender;
    private String address;
    private String phoneNumber;

}