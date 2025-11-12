package com.bvv.filegeneration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service de génération de fichiers XML (format Acquittements)
 */
@Service
@Slf4j
public class XmlFileGeneratorService {

    private final Random random = new Random();

    private static final String[] CORRELATION_IDS = {
            "BOND5-A1-035_00000D61", "GNOR8-A2-120_00000F3A", "DEVICE001_00000001",
            "DEVICE002_00000002", "DEVICE003_00000003", "DEVICE004_00000004"
    };

    private static final String[] EQUIP_IDS = {
            "BOND5-A1-035", "GNOR8-A2-120", "DEVICE001", "DEVICE002", "DEVICE003", "DEVICE004"
    };

    /**
     * Génère un fichier XML avec configuration des erreurs et doublons
     */
    public String generateXml(int totalLines, int errorLines, List<String> errorTypes, int duplicateLines) {
        try {
            StringBuilder xml = new StringBuilder();

            // En-tête XML
            xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            xml.append("<AcquittementsLN xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                    .append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ")
                    .append("xsi:schemaLocation=\"FLUX_ACQLN_C4_ATTR_v1.xsd\">\n");

            // En-tête
            xml.append("  <Entete DateGeneration=\"").append(generateValidDateTime()).append("\" ")
                    .append("NumFichier=\"").append(random.nextInt(1000)).append("\" ")
                    .append("CorrelationId=\"").append(UUID.randomUUID()).append("\" ")
                    .append("NomInterface=\"ACQLN\" VersionInterface=\"01\" />\n");

            // Générer les lignes valides
            List<String> validAcquittements = new ArrayList<>();
            int validLines = totalLines - errorLines;

            for (int i = 0; i < validLines; i++) {
                validAcquittements.add(generateValidAcquittement());
            }

            // Ajouter les lignes valides
            for (String acquittement : validAcquittements) {
                xml.append(acquittement);
            }

            // Générer et ajouter les lignes avec erreurs
            for (int i = 0; i < errorLines; i++) {
                xml.append(generateAcquittementWithErrors(errorTypes));
            }

            // Ajouter les doublons
            if (duplicateLines > 0 && !validAcquittements.isEmpty()) {
                for (int i = 0; i < duplicateLines; i++) {
                    int indexToDuplicate = random.nextInt(validAcquittements.size());
                    xml.append(validAcquittements.get(indexToDuplicate));
                }
            }

            // Fermeture
            xml.append("</AcquittementsLN>");

            return xml.toString();

        } catch (Exception e) {
            log.error("Erreur génération XML", e);
            throw new RuntimeException("Erreur génération XML: " + e.getMessage());
        }
    }

    /**
     * Génère un acquittement valide
     */
    private String generateValidAcquittement() {
        StringBuilder sb = new StringBuilder();
        String correlationId = CORRELATION_IDS[random.nextInt(CORRELATION_IDS.length)];
        String equipId = EQUIP_IDS[random.nextInt(EQUIP_IDS.length)];
        String dateInitiale = generateDateInitiale();
        String horodateGeneration = generateValidDateTime().substring(0, 19);
        String horodateReception = generateValidDateTime().substring(0, 19);

        sb.append("  <AcquittementLN>\n");
        sb.append("    <CorrelationIdACT>").append(escapeXml(correlationId)).append("</CorrelationIdACT>\n");
        sb.append("    <IdEqpt>").append(escapeXml(equipId)).append("</IdEqpt>\n");
        sb.append("    <DateInitiale>").append(dateInitiale).append("</DateInitiale>\n");
        sb.append("    <HorodateGenerationSIMTCAB>").append(horodateGeneration).append("</HorodateGenerationSIMTCAB>\n");
        sb.append("    <HorodateReceptionEquipement>").append(horodateReception).append("</HorodateReceptionEquipement>\n");
        sb.append("    <NumeroFichierFluxC2>").append(random.nextInt(1000)).append("</NumeroFichierFluxC2>\n");
        sb.append("    <VersionPARAMTT11>01DC</VersionPARAMTT11>\n");
        sb.append("    <VersionPARAMTT12>01DC</VersionPARAMTT12>\n");
        sb.append("    <VersionInterneSI>").append(2900 + random.nextInt(100)).append("</VersionInterneSI>\n");
        sb.append("  </AcquittementLN>\n");

        return sb.toString();
    }

    /**
     * Génère un acquittement avec erreurs spécifiques à ACQ MTCAB
     */
    private String generateAcquittementWithErrors(List<String> errorTypes) {
        StringBuilder sb = new StringBuilder();

        String correlationId = CORRELATION_IDS[random.nextInt(CORRELATION_IDS.length)];
        String equipId = EQUIP_IDS[random.nextInt(EQUIP_IDS.length)];
        String dateInitiale = generateDateInitiale();

        boolean hasError = errorTypes != null && !errorTypes.isEmpty();

        sb.append("  <AcquittementLN>\n");

        // CorrelationIdACT
        sb.append("    <CorrelationIdACT>").append(escapeXml(correlationId)).append("</CorrelationIdACT>\n");

        // IdEqpt
        if (hasError && errorTypes.contains("EMPTY_IDEQPT")) {
            sb.append("    <IdEqpt></IdEqpt>\n");
        } else {
            sb.append("    <IdEqpt>").append(escapeXml(equipId)).append("</IdEqpt>\n");
        }

        // DateInitiale
        sb.append("    <DateInitiale>").append(dateInitiale).append("</DateInitiale>\n");

        // HorodateGenerationSIMTCAB
        sb.append("    <HorodateGenerationSIMTCAB>").append(generateValidDateTime().substring(0, 19)).append("</HorodateGenerationSIMTCAB>\n");

        // HorodateReceptionEquipement
        if (hasError && errorTypes.contains("EMPTY_RECEPTION_DATE")) {
            sb.append("    <HorodateReceptionEquipement></HorodateReceptionEquipement>\n");
        } else if (hasError && errorTypes.contains("INVALID_RECEPTION_DATE")) {
            sb.append("    <HorodateReceptionEquipement>2023-12-12T25:55:15</HorodateReceptionEquipement>\n");
        } else {
            sb.append("    <HorodateReceptionEquipement>").append(generateValidDateTime().substring(0, 19)).append("</HorodateReceptionEquipement>\n");
        }

        // NumeroFichierFluxC2
        if (hasError && errorTypes.contains("EMPTY_NUMERO_FICHIER")) {
            sb.append("    <NumeroFichierFluxC2></NumeroFichierFluxC2>\n");
        } else if (hasError && errorTypes.contains("NUMERO_FICHIER_WRONG_LENGTH")) {
            // Générer un nombre qui n'a pas exactement 3 chiffres
            int wrongNumber = random.nextBoolean() ? random.nextInt(100) : 1000 + random.nextInt(1000);
            sb.append("    <NumeroFichierFluxC2>").append(wrongNumber).append("</NumeroFichierFluxC2>\n");
        } else {
            sb.append("    <NumeroFichierFluxC2>").append(100 + random.nextInt(900)).append("</NumeroFichierFluxC2>\n");
        }

        sb.append("    <VersionPARAMTT11>01DC</VersionPARAMTT11>\n");
        sb.append("    <VersionPARAMTT12>01DC</VersionPARAMTT12>\n");
        sb.append("    <VersionInterneSI>").append(2900 + random.nextInt(100)).append("</VersionInterneSI>\n");
        sb.append("  </AcquittementLN>\n");

        return sb.toString();
    }

    /**
     * Génère une date valide au format YYYYMMDD
     */
    private String generateDateInitiale() {
        LocalDateTime now = LocalDateTime.now().minusDays(random.nextInt(30));
        return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * Génère une date/heure valide
     */
    private String generateValidDateTime() {
        LocalDateTime now = LocalDateTime.now().minusDays(random.nextInt(30));
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    /**
     * Échappe les caractères spéciaux XML
     */
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}

