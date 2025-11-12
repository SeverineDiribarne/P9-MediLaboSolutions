package com.medilabo.medilabo_back_risk.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class AgeUtils {

    // Formats courants: ISO (2020-12-31) et FR (31/12/2020). Ajoute-en si besoin.
    private static final List<DateTimeFormatter> SUPPORTED_FORMATS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,                   // yyyy-MM-dd
        // Formats supplémentaires
        DateTimeFormatter.ofPattern("yyyy/MM/dd"), // 2020/05/01
        DateTimeFormatter.ofPattern("yyyy.MM.dd"), // 2020.05.01
        DateTimeFormatter.ofPattern("dd-MM-yyyy"), // 01-05-2020
        DateTimeFormatter.ofPattern("dd.MM.yyyy"), // 31.12.2020
        DateTimeFormatter.ofPattern("dd/MM/yyyy"), // 01/05/2020
        DateTimeFormatter.ofPattern("MM/dd/yyyy"), // 12/31/2020
        DateTimeFormatter.ofPattern("MM.dd.yyyy"), // 12.31.2020
        DateTimeFormatter.ofPattern("MM-dd-yyyy"), // 12-31-2020
        DateTimeFormatter.ofPattern("yyyy/M/d"),   // 2020/5/1
        DateTimeFormatter.ofPattern("yyyy-M-d"),  // 2020-5-1
        DateTimeFormatter.ofPattern("yyyy.M.d"),  // 2020.5.1
        DateTimeFormatter.ofPattern("d-M-yyyy"),  // 1-5-2020
        DateTimeFormatter.ofPattern("d.M.yyyy"),  // 1.5.2020
        DateTimeFormatter.ofPattern("d/M/yyyy"),  // 1/5/2020
        DateTimeFormatter.ofPattern("M/d/yyyy"),  // 5/1/2020
        DateTimeFormatter.ofPattern("M.d.yyyy"),  // 5.1.2020
        DateTimeFormatter.ofPattern("M-d-yyyy")   // 5-1-2020
    );

    private AgeUtils() {}

    public static int computeAge(String birthdateStr) {
        LocalDate dob = parseBirthdate(birthdateStr);
        if (dob == null) {
            // À toi de voir: lancer une exception, retourner -1, etc.
            return -1;
        }
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        if (dob.isAfter(today)) {
            return -1; 
        }
        return Period.between(dob, today).getYears();
    }

    public static LocalDate parseBirthdate(String birthdateStr) {
        if (birthdateStr == null || birthdateStr.isBlank()) return null;
        for (DateTimeFormatter f : SUPPORTED_FORMATS) {
            try {
                return LocalDate.parse(birthdateStr.trim(), f);
            } catch (DateTimeParseException ignored) {
                // on essaie le format suivant
            }
        }
        return null; // aucun format n’a matché
    }
}