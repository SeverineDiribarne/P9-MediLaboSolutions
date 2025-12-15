package com.medilabo.medilabo.services.patientService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.medilabo.medilabo.model.Gender;
import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.repositories.IPatientRepository;

class PatientServiceTest {

    private Patient sample(long id) {
        return new Patient(id, "Doe", "John", "1980-01-01", Gender.M, "1 st", "0102030405");
    }

    @Test
    @DisplayName("getPatientList délègue à repository.findAll")
    void getPatientList_ok() {
        PatientService svc = new PatientService();
        IPatientRepository repo = mock(IPatientRepository.class);
        svc.patientRepository = repo; // champ package-private

        when(repo.findAll()).thenReturn(List.of(sample(1), sample(2)));

        Iterable<Patient> out = svc.getPatientList();
        assertNotNull(out);
        assertEquals(2, ((List<?>) out).size());
        verify(repo).findAll();
    }

    @Test
    @DisplayName("getPatientById délègue à repository.findById")
    void getPatientById_ok() {
        PatientService svc = new PatientService();
        IPatientRepository repo = mock(IPatientRepository.class);
        svc.patientRepository = repo;

        when(repo.findById(1L)).thenReturn(Optional.of(sample(1)));
        Optional<Patient> out = svc.getPatientById(1L);
        assertTrue(out.isPresent());
        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("savePatient délègue à repository.save")
    void savePatient_ok() {
        PatientService svc = new PatientService();
        IPatientRepository repo = mock(IPatientRepository.class);
        svc.patientRepository = repo;

        Patient in = sample(0);
        Patient saved = sample(10);
        when(repo.save(in)).thenReturn(saved);

        Patient out = svc.savePatient(in);
        assertEquals(saved, out);
        verify(repo).save(in);
    }

    @Test
    @DisplayName("updatePatient lève IllegalArgumentException si patient introuvable")
    void updatePatient_notFound() {
        PatientService svc = new PatientService();
        IPatientRepository repo = mock(IPatientRepository.class);
        svc.patientRepository = repo;

        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> svc.updatePatient(99L, sample(0)));
    }

    @Test
    @DisplayName("updatePatient met à jour les champs et conserve l'id")
    void updatePatient_ok() {
        PatientService svc = new PatientService();
        IPatientRepository repo = mock(IPatientRepository.class);
        svc.patientRepository = repo;

        Patient existing = sample(5);
        when(repo.findById(5L)).thenReturn(Optional.of(existing));

        Patient updated = new Patient(0, "Smith", "Alice", "1990-02-02", Gender.F, "2 av", "0600000000");
        Patient expectedSaved = new Patient(5, updated.getLastname(), updated.getFirstname(), updated.getBirthdate(), updated.getGender(), updated.getAddress(), updated.getPhoneNumber());
        when(repo.save(any(Patient.class))).thenReturn(expectedSaved);

        Patient out = svc.updatePatient(5L, updated);
        assertEquals(5, out.getPatientId());
        assertEquals("Smith", out.getLastname());
        assertEquals(Gender.F, out.getGender());
        verify(repo).save(any(Patient.class));
    }
}
