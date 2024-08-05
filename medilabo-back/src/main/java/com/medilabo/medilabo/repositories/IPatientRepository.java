package com.medilabo.medilabo.repositories;

import com.medilabo.medilabo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;



public interface IPatientRepository extends JpaRepository<Patient, Long> {}
