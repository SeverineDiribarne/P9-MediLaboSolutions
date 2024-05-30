package com.medilabo.medilabo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;



@Getter
@ToString
@Entity
@Table(name="patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patientId")
    private  long id = 0L;

    @Column(name="lastname")
    private String lastname ="";

    @Column(name="firstname")
    private String firstname = "";

    @Column(name="birthdate")
    private String birthdate = "";

    @Column(name="gender")
    private Gender gender = Gender.NONE;

    @Column(name="address")
    private String address = "";

    @Column(name="phoneNumber")
    private String phoneNumber ="";

    public Patient(long id, String lastname, String firstname, String birthdate, Gender gender, String address, String phoneNumber) {
        this.id = id;
        this.lastname=lastname;
        this.firstname = firstname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
