package com.medilabo.medilabo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patientId")
    private final long id = 0L;

    @Column(name="lastname")
    private final String lastname ="";

    @Column(name="firstname")
    private final String firstname = "";

    @Column(name="birthdate")
    private final String birthdate = "";

    @Column(name="gender")
    private final Gender gender = Gender.NONE;

    @Column(name="address")
    private final String address = "";

    @Column(name="phoneNumber")
    private final String phoneNumber ="";
}
