package com.medilabo_gui.medilabo_gui.model;

import com.medilabo_gui.medilabo_gui.model.Gender;
import lombok.Getter;
import lombok.ToString;


    @Getter
    @ToString
    public class Patient {

        private  long patientId;

        private String lastname ="";

        private String firstname = "";

        private String birthdate = "";

        private Gender gender= Gender.NONE;

        private String address = "";

        private String phoneNumber ="";

        public Patient(){}

        public Patient(long patientId, String lastname, String firstname, String birthdate, Gender gender, String address, String phoneNumber) {
            this.patientId = patientId;
            this.lastname=lastname;
            this.firstname = firstname;
            this.birthdate = birthdate;
            this.gender = gender;
            this.address = address;
            this.phoneNumber = phoneNumber;
        }

    }