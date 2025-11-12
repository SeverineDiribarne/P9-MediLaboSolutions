package com.medilabo.medilabo.exceptions;

public class PatientNotFoundException extends RuntimeException {
 public PatientNotFoundException(Long id) {
        super("Patient not found with id: " + id);
    }
}
