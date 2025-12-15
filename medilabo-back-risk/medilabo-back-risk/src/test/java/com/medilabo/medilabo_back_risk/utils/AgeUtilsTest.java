package com.medilabo.medilabo_back_risk.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AgeUtilsTest {

    @Test
    @DisplayName("computeAge retourne l'âge attendu pour une date ISO")
    void computeAgeIso() {
        int years = 40;
        String birth = LocalDate.now().minusYears(years).format(DateTimeFormatter.ISO_LOCAL_DATE);
        assertEquals(years, AgeUtils.computeAge(birth));
    }

    @Test
    @DisplayName("computeAge gère les formats multiples (dd/MM/yyyy)")
    void computeAgeMultipleFormats() {
        // 2000/01/01 en format dd/MM/yyyy
        String birth = "01/01/2000";
        int expected = AgeUtils.computeAge("2000-01-01"); // référence ISO
        assertEquals(expected, AgeUtils.computeAge(birth));
    }

    @Test
    @DisplayName("computeAge renvoie -1 pour null, vide ou date future")
    void computeAgeInvalid() {
        assertEquals(-1, AgeUtils.computeAge(null));
        assertEquals(-1, AgeUtils.computeAge(" "));
        String future = LocalDate.now().plusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        assertEquals(-1, AgeUtils.computeAge(future));
    }

    @Test
    @DisplayName("parseBirthdate reconnaît plusieurs formats")
    void parseFormats() {
        assertNotNull(AgeUtils.parseBirthdate("2000-05-01"));
        assertNotNull(AgeUtils.parseBirthdate("01/05/2000"));
        assertNotNull(AgeUtils.parseBirthdate("01-05-2000"));
        assertNotNull(AgeUtils.parseBirthdate("2000/05/01"));
        assertNull(AgeUtils.parseBirthdate("invalid"));
    }
}
