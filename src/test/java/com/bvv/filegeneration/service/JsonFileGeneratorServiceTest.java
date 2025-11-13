package com.bvv.filegeneration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le service de génération de fichiers JSON
 */
@DisplayName("JsonFileGeneratorService Tests")
public class JsonFileGeneratorServiceTest {

    private JsonFileGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new JsonFileGeneratorService();
    }

    @Test
    @DisplayName("Générer un fichier valide sans erreurs")
    void testGenerateValidJsonWithoutErrors() {
        String json = generatorService.generateJson(10, 0, null, 0);

        assertNotNull(json);
        assertTrue(json.contains("\"equipments\""));
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"file\""));
        assertTrue(json.contains("\"deployDateTime\""));

        // Vérifier que c'est du JSON valide
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
    }

    @Test
    @DisplayName("Générer un fichier avec erreurs spécifiques")
    void testGenerateJsonWithErrors() {
        List<String> errorTypes = Arrays.asList("EMPTY_FIELD", "INVALID_DATE");
        String json = generatorService.generateJson(10, 2, errorTypes, 0);

        assertNotNull(json);
        assertTrue(json.contains("\"equipments\""));

        // Vérifier la présence d'erreurs (champs vides ou dates invalides)
        boolean hasEmptyField = json.contains("\"\"");
        boolean hasInvalidDate = json.contains("2025-13-32") || json.contains("2025-12-32T25");

        assertTrue(hasEmptyField || hasInvalidDate,
                "Le JSON devrait contenir des erreurs");
    }

    @Test
    @DisplayName("Générer un fichier avec doublons")
    void testGenerateJsonWithDuplicates() {
        String json = generatorService.generateJson(5, 0, null, 2);

        assertNotNull(json);

        // Le total de lignes doit être 5 (originales) + 2 (doublons) = 7
        int equipmentCount = countOccurrences(json, "\"name\"");
        assertEquals(7, equipmentCount,
                "Le fichier devrait avoir 5 lignes originales + 2 doublons = 7");
    }

    @Test
    @DisplayName("Générer un fichier avec erreurs et doublons")
    void testGenerateJsonWithErrorsAndDuplicates() {
        List<String> errorTypes = Arrays.asList("NULL_VALUE");
        String json = generatorService.generateJson(10, 3, errorTypes, 2);

        assertNotNull(json);
        assertTrue(json.contains("\"equipments\""));

        // Le total doit être 10 (originales) - 3 (erreurs) + 3 (erreurs) + 2 (doublons) = 12 lignes
        int equipmentCount = countOccurrences(json, "\"name\"");
        assertEquals(12, equipmentCount,
                "Le fichier devrait avoir 10 + 2 doublons = 12 lignes");
    }

    @Test
    @DisplayName("Tous les types d'erreurs sont gérés")
    void testAllErrorTypesAreHandled() {
        List<String> allErrorTypes = Arrays.asList(
                "NULL_VALUE", "EMPTY_FIELD", "INVALID_DATE", "OUT_OF_BOUNDS",
                "WRONG_TYPE", "TOO_LONG", "TOO_SHORT", "INVALID_FORMAT", "MISSING_FIELD"
        );

        String json = generatorService.generateJson(20, 5, allErrorTypes, 0);

        assertNotNull(json);
        assertTrue(json.contains("\"equipments\""));

        // Vérifier que le JSON contient au moins quelques erreurs
        boolean hasErrors = json.contains("\"\"") ||
                          json.contains("null") ||
                          json.contains("2025-13") ||
                          !json.contains("\"deployDateTime\"");

        assertTrue(hasErrors, "Le JSON devrait contenir des erreurs");
    }

    @Test
    @DisplayName("Générer un minimum de 1 ligne")
    void testGenerateMinimumLines() {
        String json = generatorService.generateJson(1, 0, null, 0);

        assertNotNull(json);
        int equipmentCount = countOccurrences(json, "\"name\"");
        assertEquals(1, equipmentCount);
    }

    @Test
    @DisplayName("Générer un fichier avec beaucoup de lignes")
    void testGenerateManyLines() {
        String json = generatorService.generateJson(100, 10, null, 5);

        assertNotNull(json);
        // 100 - 10 (erreurs) + 10 (erreurs) + 5 (doublons) = 105 lignes
        int equipmentCount = countOccurrences(json, "\"name\"");
        assertEquals(105, equipmentCount);
    }

    @Test
    @DisplayName("JSON est bien formaté")
    void testJsonIsWellFormatted() {
        String json = generatorService.generateJson(5, 0, null, 0);

        assertNotNull(json);
        // Vérifier qu'il y a une indentation (pretty print)
        assertTrue(json.contains("\n"), "JSON devrait être formaté avec des retours à la ligne");
        assertTrue(json.contains("  "), "JSON devrait avoir une indentation");
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

