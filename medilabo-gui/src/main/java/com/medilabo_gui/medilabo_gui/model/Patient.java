package com.medilabo_gui.medilabo_gui.model;

import java.util.List;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Patient {

    private long patientId;

    private String lastname;

    private String firstname;

    private String birthdate;

    private Gender gender;

    private String address;

    private String phoneNumber;

    private List<Note> notes;
}
