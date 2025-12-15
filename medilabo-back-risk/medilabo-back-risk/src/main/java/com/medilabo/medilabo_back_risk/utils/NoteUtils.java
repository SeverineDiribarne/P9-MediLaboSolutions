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
 * Utilities related to notes for detecting trigger words.
 * All methods are instance methods to allow dependency injection if needed.
 */
@Component
public class NoteUtils {

    /**
     * Model based on "concepts" rather than isolated words to group
     * synonyms/variations (gender, number, verb) under a single clinical entity.
     * Each concept is counted at most ONCE even if multiple synonyms appear.
     */
    private final Map<String, List<String>> CONCEPT_SYNONYMS_ORIGINAL = Map.ofEntries(
        Map.entry("hémoglobine A1C", List.of("hémoglobine A1C")),
        Map.entry("microalbumine", List.of("microalbumine")),
        Map.entry("taille", List.of("taille")),
        Map.entry("poids", List.of("poids")),
        Map.entry("fumeur", List.of("fumeur", "fumeuse", "fumer")),
        Map.entry("anormal", List.of("anormal", "anormale", "anormaux", "anormales")),
        Map.entry("cholestérol", List.of("cholestérol")),
        Map.entry("vertige", List.of("vertige", "vertiges")),
        Map.entry("rechute", List.of("rechute")),
        Map.entry("réaction", List.of("réaction", "reactions", "reaction")),
        Map.entry("anticorps", List.of("anticorps"))
    );

    // Immutable list of concepts (canonical name)
    private final List<String> CONCEPTS = Collections.unmodifiableList(new ArrayList<>(CONCEPT_SYNONYMS_ORIGINAL.keySet()));

    // Map concept -> list of normalized synonyms
    private final Map<String, List<String>> NORMALIZED_CONCEPT_SYNONYMS;

    // List of normalized forms of canonical names (for backward compatibility of existing tests)
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
     * Returns the immutable list of trigger words (original form).
     */
    public List<String> getTriggerWords() { // compatibility with old API
        return CONCEPTS; // returns the canonical names of concepts
    }

    /**
     * Returns the immutable list of normalized trigger words (no accents, lowercase).
     */
    public List<String> getNormalizedTriggerWords() { // compatibility with old API
        return NORMALIZED_CONCEPT_NAMES;
    }

    /**
     * New accessor: returns the map concept -> normalized synonyms.
     */
    public Map<String, List<String>> getNormalizedConceptSynonyms() {
        return NORMALIZED_CONCEPT_SYNONYMS;
    }

    /**
     * Normalizes a string: lowercases and removes diacritics (accents).
     */
    public String normalize(String text) {
        if (text == null)
            return "";
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        // remove diacritic marks
        normalizedText = normalizedText.replaceAll("\\p{M}+", "");
        return normalizedText.toLowerCase();
    }

   
    /**
     * Returns a Map containing, for each trigger word, its presence in the text (1) or not (0).
     * Each trigger word is counted at most once.
     * The Map key is the word as it appears in {@link #getTriggerWords()} (original form).
     *
     * @param text text to analyze (may be null)
     * @return Map word -> 0|1
     */
    public Map<String, Integer> mapTriggerWordCounts(String text) {
        // Kept for compatibility: each concept returns 0/1 based on presence of at least one synonym
        return mapConceptCounts(text);
    }

    /**
     * New explicit method based on concepts.
     * @param text source text (may be null)
     * @return map concept -> 0/1 (1 if at least one synonym appears)
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
                    break; // move to the next concept
                }
            }
        }
        return result;
    }
}
