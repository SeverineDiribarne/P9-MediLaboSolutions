package com.medilabo.medilabo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name="patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="patient_id")
    private  long patientId;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name="lastname")
    private String lastname ="";

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name="firstname")
    private String firstname = "";

    @NotNull
    @Column(name="birthdate")
    private String birthdate = "";

    @Column(name="gender")
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.NONE;

    @Column(name="address")
    @Size(min = 1, max = 255)
    private String address = "";

    @Column(name="phoneNumber")
    @Size(min = 10, max = 15)
    private String phoneNumber ="";

}
