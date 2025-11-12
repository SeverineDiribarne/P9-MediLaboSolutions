package com.medilabo.medilabo_back_risk.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Utilitaires liés aux notes pour la détection de mots déclencheurs.
 * Toutes les méthodes sont d'instance afin de pouvoir utiliser l'injection de dépendances si nécessaire.
 */
@Component
public class NoteUtils {

    /**
     * Modèle basé sur des "concepts" plutôt que sur des mots isolés afin de regrouper
     * les synonymes / variations (genre, nombre, verbe) sous une seule entité clinique.
     * Chaque concept est compté au plus UNE fois même si plusieurs synonymes apparaissent.
     */
    private final Map<String, List<String>> CONCEPT_SYNONYMS_ORIGINAL = Map.ofEntries(
        Map.entry("hémoglobine A1C", List.of("hémoglobine A1C")),
        Map.entry("microalbumine", List.of("microalbumine")),
        Map.entry("taille", List.of("taille")),
        Map.entry("poids", List.of("poids")),
        // tabagisme
        Map.entry("fumeur", List.of("fumeur", "fumeuse", "fumer")),
        // anormalités (on garde le terme principal pour ne pas complexifier la règle métier)
        Map.entry("anormal", List.of("anormal", "anormale", "anormaux", "anormales")),
        Map.entry("cholestérol", List.of("cholestérol")),
        Map.entry("vertige", List.of("vertige", "vertiges")),
        Map.entry("rechute", List.of("rechute")),
        Map.entry("réaction", List.of("réaction", "reactions", "reaction")),
        Map.entry("anticorps", List.of("anticorps"))
    );

    // Liste immuable des concepts (nom canonique)
    private final List<String> CONCEPTS = Collections.unmodifiableList(new ArrayList<>(CONCEPT_SYNONYMS_ORIGINAL.keySet()));

    // Map concept -> liste des synonymes normalisés
    private final Map<String, List<String>> NORMALIZED_CONCEPT_SYNONYMS;

    // Liste des formes normalisées des noms canoniques (pour compat rétro des tests existants)
    private final List<String> NORMALIZED_CONCEPT_NAMES;

    public NoteUtils() {
        Map<String, List<String>> tmp = new LinkedHashMap<>();
        for (String concept : CONCEPTS) {
            List<String> normalizedSyns = CONCEPT_SYNONYMS_ORIGINAL.get(concept).stream()
                    .map(this::normalize)
                    .distinct()
                    .collect(Collectors.toList());
            tmp.put(concept, normalizedSyns);
        }
        this.NORMALIZED_CONCEPT_SYNONYMS = Collections.unmodifiableMap(tmp);
        this.NORMALIZED_CONCEPT_NAMES = Collections.unmodifiableList(
                CONCEPTS.stream().map(this::normalize).collect(Collectors.toList())
        );
    }

    /**
     * Retourne la liste immuable des mots déclencheurs (forme originale).
     */
    public List<String> getTriggerWords() { // compatibilité avec ancienne API
        return CONCEPTS; // retourne les noms canoniques des concepts
    }

    /**
     * Retourne la liste immuable des mots déclencheurs normalisés (sans accents, en minuscules).
     */
    public List<String> getNormalizedTriggerWords() { // compat héritée
        return NORMALIZED_CONCEPT_NAMES;
    }

    /**
     * Nouvel accès : retourne la map concept -> synonymes normalisés.
     */
    public Map<String, List<String>> getNormalizedConceptSynonyms() {
        return NORMALIZED_CONCEPT_SYNONYMS;
    }

    /**
     * Normalise une chaîne : met en minuscule et supprime les diacritiques (accents).
     */
    public String normalize(String text) {
        if (text == null)
            return "";
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        // supprime les marques diacritiques
        normalizedText = normalizedText.replaceAll("\\p{M}+", "");
        return normalizedText.toLowerCase();
    }

    // /**
    //  * Vérifie si le texte fourni contient au moins un mot déclencheur (insensible à la casse et aux accents).
    //  *
    //  * @param text le texte à analyser (peut être null)
    //  * @return true si un mot déclencheur est présent, false sinon
    //  */
    // public boolean containsTriggerWord(String text) {
    //     if (text == null || text.isBlank()) {
    //         return false;
    //     }
    //     String normalizedText = normalize(text);
    //     for (String normalizedWord : NORMALIZED_TRIGGER_WORDS) {
    //         if (normalizedText.contains(normalizedWord)) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    /**
     * Retourne une Map contenant pour chaque mot déclencheur sa présence dans le texte (1) ou non (0).
     * Chaque mot déclencheur est compté au maximum une fois.
     * La clé de la Map est le mot tel qu'il apparaît dans {@link #getTriggerWords()} (forme originale).
     *
     * @param text le texte à analyser (peut être null)
     * @return Map mot -> 0|1
     */
    public Map<String, Integer> mapTriggerWordCounts(String text) {
        // Conservé pour compatibilité : chaque concept renvoie 0/1 selon présence d'au moins un synonyme
        return mapConceptCounts(text);
    }

    /**
     * Nouvelle méthode explicite basée sur les concepts.
     * @param text texte source (peut être null)
     * @return map concept -> 0/1 (1 si au moins un synonyme apparaît)
     */
    public Map<String, Integer> mapConceptCounts(String text) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (String concept : CONCEPTS) {
            result.put(concept, 0);
        }
        if (text == null || text.isBlank()) {
            return result;
        }
        String normalizedText = normalize(text);
        for (Map.Entry<String, List<String>> entry : NORMALIZED_CONCEPT_SYNONYMS.entrySet()) {
            for (String syn : entry.getValue()) {
                if (normalizedText.contains(syn)) {
                    result.put(entry.getKey(), 1);
                    break; // on passe au concept suivant
                }
            }
        }
        return result;
    }
}
