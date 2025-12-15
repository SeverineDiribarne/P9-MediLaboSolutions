package com.medilabo.medilabo_back_risk.dto;

import com.medilabo.medilabo_back_risk.model.Gender;
import com.medilabo.medilabo_back_risk.utils.AgeUtils;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    @NotNull
    private String birthdate = "";

    @NotNull
    private int age =0;

    @NotNull
    private Gender gender = Gender.M;

    
    //Treatment of the patient's age
    public int getPatientAge(PatientDTO dto) {
        return AgeUtils.computeAge(dto.getBirthdate());
    }
}