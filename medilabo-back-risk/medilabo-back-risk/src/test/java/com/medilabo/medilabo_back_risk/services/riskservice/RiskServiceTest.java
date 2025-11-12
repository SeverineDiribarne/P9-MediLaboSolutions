package com.medilabo.medilabo_back_risk.services.riskservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.medilabo.medilabo_back_risk.dto.PatientDTO;
import com.medilabo.medilabo_back_risk.model.Gender;

class RiskServiceTest {

    private RiskService svc;

    @BeforeEach
    void setup() {
        svc = new RiskService();
    }

    private static PatientDTO patient(int age, char genderChar) {
        PatientDTO dto = new PatientDTO();
        dto.setAge(age);
        dto.setGender(genderChar == 'M' ? Gender.M : (genderChar == 'F' ? Gender.F : Gender.X));
        return dto;
    }

    @ParameterizedTest(name = "NONE -> age={0}, gender={1}, triggers={2}")
    @CsvSource({
        "50,M,0",
        "50,M,1",
        "50,F,1",
        "50,F,0",
        "50,X,0",
        "50,X,1",
        "25,M,0",
        "25,M,1",
        "25,F,0",
        "25,F,1",
        "25,X,0",
        "25,X,1"
    })
    @DisplayName("NONE quand count <= 1")
    void noneWhenCountLeOne(int age, char gender, int triggers) {
        assertEquals("NONE", svc.processRiskData(patient(age, gender), triggers));
    }

    @ParameterizedTest(name = "BORDERLINE -> age={0}, gender={1}, triggers={2}")
    @CsvSource({
        "31,M,2",
        "45,F,5"
    })
    @DisplayName("BORDERLINE quand 2<=count<6 et age>30")
    void borderlineAdultBetween2And5(int age, char gender, int triggers) {
        assertEquals("BORDERLINE", svc.processRiskData(patient(age, gender), triggers));
    }

    @ParameterizedTest(name = "IN_DANGER young -> age={0}, gender={1}, triggers={2}")
    @CsvSource({
        "25,M,3",
        "29,F,4"
    })
    @DisplayName("IN_DANGER: homme <30 avec 3; femme <30 avec 4")
    void inDangerYoungWithSpecificCounts(int age, char gender, int triggers) {
        assertEquals("IN_DANGER", svc.processRiskData(patient(age, gender), triggers));
    }

    @ParameterizedTest(name = "IN_DANGER adult -> age={0}, gender={1}, triggers={2}")
    @CsvSource({
        "30,M,6",
        "60,F,7"
    })
    @DisplayName("IN_DANGER: age>=30 avec 6 à 7")
    void inDangerAdultSixToSeven(int age, char gender, int triggers) {
        assertEquals("IN_DANGER", svc.processRiskData(patient(age, gender), triggers));
    }

    @ParameterizedTest(name = "EARLY_ONSET young -> age={0}, gender={1}, triggers={2}")
    @CsvSource({
        "20,M,5",
        "19,F,7"
    })
    @DisplayName("EARLY_ONSET: homme <30 avec 5; femme <30 avec 7")
    void earlyOnsetYoungSpecific(int age, char gender, int triggers) {
        assertEquals("EARLY_ONSET", svc.processRiskData(patient(age, gender), triggers));
    }

    @ParameterizedTest(name = "EARLY_ONSET adult -> age={0}, gender={1}, triggers={2}")
    @CsvSource({
        "30,M,8",
        "75,F,10"
    })
    @DisplayName("EARLY_ONSET: age>=30 avec count>=8")
    void earlyOnsetAdultEightOrMore(int age, char gender, int triggers) {
        assertEquals("EARLY_ONSET", svc.processRiskData(patient(age, gender), triggers));
    }

    @ParameterizedTest(name = "UNDETERMINED -> age={0}, gender={1}, triggers={2}")
    @CsvSource({
        // Homme <30 avec 2 déclencheurs -> aucune règle explicite
        "25,M,2",
        // Homme <30 avec 4 déclencheurs -> aucune règle (3=IN_DANGER, 5=EARLY_ONSET)
        "25,M,4",
        // Femme <30 avec 6 déclencheurs -> aucune règle (4=IN_DANGER, 7=EARLY_ONSET)
        "29,F,6",
        // Age == 30 avec 5 déclencheurs -> pas BORDERLINE (>30) ni IN_DANGER/EARLY_ONSET
        "30,M,5"
    })
    @DisplayName("UNDETERMINED si aucune règle ne s'applique")
    void undeterminedWhenNoRuleMatches(int age, char gender, int triggers) {
        assertEquals("UNDETERMINED", svc.processRiskData(patient(age, gender), triggers));
    }
}
