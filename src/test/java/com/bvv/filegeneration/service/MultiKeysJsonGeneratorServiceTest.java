package com.bvv.filegeneration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le service de génération de fichiers JSON multi-clés (ACQ PE)
 */
@DisplayName("MultiKeysJsonGeneratorService Tests")
public class MultiKeysJsonGeneratorServiceTest {

    private MultiKeysJsonGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new MultiKeysJsonGeneratorService();
    }

    @Test
    @DisplayName("Générer un fichier JSON multi-clés valide")
    void testGenerateValidMultiKeysJson() {
        String json = generatorService.generateMultiKeysJson(10, 0, null, 0);

        assertNotNull(json);
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        assertTrue(json.contains("PNO"));
        assertTrue(json.contains("deployDateTime"));
        assertTrue(json.contains("fetchDateTime"));
        assertTrue(json.contains("file"));
        assertTrue(json.contains("version"));
    }

    @Test
    @DisplayName("Générer un fichier JSON multi-clés avec erreurs")
    void testGenerateMultiKeysJsonWithErrors() {
        List<String> errorTypes = Arrays.asList("EMPTY_FIELD", "INVALID_DATE");
        String json = generatorService.generateMultiKeysJson(10, 2, errorTypes, 0);

        assertNotNull(json);

        // Vérifier la présence d'erreurs (champs vides ou dates invalides)
        boolean hasEmptyField = json.contains("\"\"");
        boolean hasInvalidDate = json.contains("2025-13-32") || json.contains("25:61");

        assertTrue(hasEmptyField || hasInvalidDate,
                "Le JSON devrait contenir des erreurs");
    }

    @Test
    @DisplayName("Générer un fichier JSON multi-clés avec doublons")
    void testGenerateMultiKeysJsonWithDuplicates() {
        String json = generatorService.generateMultiKeysJson(5, 0, null, 2);

        assertNotNull(json);

        // Le total doit être 5 + 2 (doublons) = 7
        int count = countOccurrences(json, "deployDateTime");
        assertEquals(7, count,
                "Le fichier devrait avoir 5 + 2 doublons = 7 enregistrements");
    }

    @Test
    @DisplayName("Générer un fichier JSON multi-clés avec erreurs et doublons")
    void testGenerateMultiKeysJsonWithErrorsAndDuplicates() {
        List<String> errorTypes = Arrays.asList("INVALID_DATE");
        String json = generatorService.generateMultiKeysJson(8, 2, errorTypes, 1);

        assertNotNull(json);

        // Le total doit être 8 + 1 (doublon) = 9
        int count = countOccurrences(json, "deployDateTime");
        assertEquals(9, count,
                "Le fichier devrait avoir 8 + 1 doublon = 9 enregistrements");
    }

    @Test
    @DisplayName("Structure JSON multi-clés valide")
    void testMultiKeysJsonStructure() {
        String json = generatorService.generateMultiKeysJson(3, 0, null, 0);

        assertNotNull(json);

        // Vérifier la présence de clés PNO
        assertTrue(json.contains("\"PNO"),
                "Le JSON devrait contenir des clés PNO");

        // Vérifier la structure des objets
        assertTrue(json.contains("deployDateTime"),
                "Doit avoir deployDateTime");
        assertTrue(json.contains("fetchDateTime"),
                "Doit avoir fetchDateTime");
        assertTrue(json.contains("file"),
                "Doit avoir file");
        assertTrue(json.contains("version"),
                "Doit avoir version");
    }

    @Test
    @DisplayName("JSON est bien formaté (pretty print)")
    void testJsonIsWellFormatted() {
        String json = generatorService.generateMultiKeysJson(2, 0, null, 0);

        assertNotNull(json);
        // Vérifier qu'il y a une indentation
        assertTrue(json.contains("\n"), "JSON devrait avoir des retours à la ligne");
        assertTrue(json.contains("    "), "JSON devrait avoir une indentation");
    }

    @Test
    @DisplayName("Tous les types d'erreurs gérés")
    void testAllErrorTypesHandled() {
        List<String> allErrorTypes = Arrays.asList(
                "EMPTY_FIELD", "INVALID_DATE", "MISSING_FIELD",
                "WRONG_TYPE", "TOO_SHORT"
        );

        String json = generatorService.generateMultiKeysJson(10, 3, allErrorTypes, 0);

        assertNotNull(json);
        assertTrue(json.contains("deployDateTime"));
        assertTrue(json.contains("fetchDateTime"));
    }

    /**
     * Utilitaire pour compter les occurrences d'une chaîne
     */
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }
}

