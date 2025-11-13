package com.bvv.filegeneration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le service de génération de fichiers XML
 */
@DisplayName("XmlFileGeneratorService Tests")
public class XmlFileGeneratorServiceTest {

    private XmlFileGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new XmlFileGeneratorService();
    }

    @Test
    @DisplayName("Générer un fichier XML valide sans erreurs")
    void testGenerateValidXmlWithoutErrors() {
        String xml = generatorService.generateXml(5, 0, null, 0);

        assertNotNull(xml);
        assertTrue(xml.startsWith("<?xml"));
        assertTrue(xml.contains("<AcquittementsLN"));
        assertTrue(xml.contains("<Entete"));
        assertTrue(xml.contains("<AcquittementLN>"));
        assertTrue(xml.contains("</AcquittementsLN>"));

        // Vérifier le nombre d'acquittements
        int count = countOccurrences(xml, "<AcquittementLN>");
        assertEquals(5, count);
    }

    @Test
    @DisplayName("Générer un fichier XML avec erreurs")
    void testGenerateXmlWithErrors() {
        List<String> errorTypes = Arrays.asList("EMPTY_FIELD", "INVALID_DATE");
        String xml = generatorService.generateXml(5, 2, errorTypes, 0);

        assertNotNull(xml);
        assertTrue(xml.contains("<AcquittementsLN"));

        // Vérifier la présence d'erreurs (champs vides ou dates invalides)
        boolean hasEmptyField = xml.contains("<CorrelationIdACT></CorrelationIdACT>") ||
                                xml.contains("<IdEqpt></IdEqpt>") ||
                                xml.contains("<HorodateReceptionEquipement></HorodateReceptionEquipement>");
        boolean hasInvalidDate = xml.contains("20231332") || xml.contains("2023-12-12T25");

        assertTrue(hasEmptyField || hasInvalidDate,
                "Le XML devrait contenir des erreurs");
    }

    @Test
    @DisplayName("Générer un fichier XML avec doublons")
    void testGenerateXmlWithDuplicates() {
        String xml = generatorService.generateXml(5, 0, null, 2);

        assertNotNull(xml);

        // Le total de lignes doit être 5 + 2 (doublons) = 7
        int count = countOccurrences(xml, "<AcquittementLN>");
        assertEquals(7, count,
                "Le fichier devrait avoir 5 + 2 doublons = 7 acquittements");
    }

    @Test
    @DisplayName("Générer un fichier XML avec erreurs et doublons")
    void testGenerateXmlWithErrorsAndDuplicates() {
        List<String> errorTypes = Arrays.asList("INVALID_DATE");
        String xml = generatorService.generateXml(10, 3, errorTypes, 2);

        assertNotNull(xml);

        // Le total doit être 10 + 2 (doublons) = 12
        int count = countOccurrences(xml, "<AcquittementLN>");
        assertEquals(12, count,
                "Le fichier devrait avoir 10 + 2 doublons = 12 acquittements");
    }

    @Test
    @DisplayName("XML est bien formé")
    void testXmlIsWellFormed() {
        String xml = generatorService.generateXml(3, 0, null, 0);

        assertNotNull(xml);
        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
        assertTrue(xml.contains("xmlns:xsi"));
        assertTrue(xml.contains("xmlns:xsd"));
        assertTrue(xml.endsWith("</AcquittementsLN>"));
    }

    @Test
    @DisplayName("En-tête XML est présent et valide")
    void testXmlHeaderIsPresent() {
        String xml = generatorService.generateXml(1, 0, null, 0);

        assertNotNull(xml);
        assertTrue(xml.contains("<Entete"));
        assertTrue(xml.contains("DateGeneration="));
        assertTrue(xml.contains("NumFichier="));
        assertTrue(xml.contains("CorrelationId="));
        assertTrue(xml.contains("NomInterface=\"ACQLN\""));
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

