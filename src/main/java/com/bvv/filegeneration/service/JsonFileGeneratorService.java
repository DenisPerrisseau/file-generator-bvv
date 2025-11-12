package com.bvv.filegeneration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service de génération de fichiers JSON
 */
@Service
@Slf4j
public class JsonFileGeneratorService {

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] DEVICE_IDS = {
            "DVIFRPNO171", "DVIFRPNO241", "GNOR8-A2-120", "BOND5-A1-035",
            "DEVICE001", "DEVICE002", "DEVICE003", "DEVICE004", "DEVICE005"
    };

    private static final String[] FILE_IDS = {
            "B804001085", "B804001086", "F123456", "F789012", "F345678",
            "F901234", "F567890", "F111222", "F333444"
    };

    /**
     * Génère un fichier JSON avec configuration des erreurs et doublons
     */
    public String generateJson(int totalLines, int errorLines, List<String> errorTypes, int duplicateLines) {
        try {
            // Générer les lignes valides
            List<Map<String, Object>> equipments = new ArrayList<>();
            List<Map<String, Object>> validLines = new ArrayList<>();

            for (int i = 0; i < (totalLines - errorLines); i++) {
                validLines.add(generateValidEquipment());
            }

            // Ajouter les lignes valides
            equipments.addAll(validLines);

            // Générer et ajouter les lignes avec erreurs
            for (int i = 0; i < errorLines; i++) {
                equipments.add(generateEquipmentWithErrors(errorTypes));
            }

            // Ajouter les doublons (lignes duplicatas parmi les lignes valides)
            if (duplicateLines > 0 && !validLines.isEmpty()) {
                for (int i = 0; i < duplicateLines; i++) {
                    int indexToDuplicate = random.nextInt(validLines.size());
                    Map<String, Object> duplicate = new LinkedHashMap<>(validLines.get(indexToDuplicate));
                    equipments.add(duplicate);
                }
            }

            // Créer la structure finale
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("equipments", equipments);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);

        } catch (Exception e) {
            log.error("Erreur génération JSON", e);
            throw new RuntimeException("Erreur génération JSON: " + e.getMessage());
        }
    }

    /**
     * Génère une ligne d'équipement valide
     */
    private Map<String, Object> generateValidEquipment() {
        Map<String, Object> equipment = new LinkedHashMap<>();
        equipment.put("name", DEVICE_IDS[random.nextInt(DEVICE_IDS.length)]);
        equipment.put("file", FILE_IDS[random.nextInt(FILE_IDS.length)]);
        equipment.put("deployDateTime", generateValidDateTime());
        return equipment;
    }

    /**
     * Génère un équipement avec erreurs spécifiques à ACQ MTBORNE
     */
    private Map<String, Object> generateEquipmentWithErrors(List<String> errorTypes) {
        Map<String, Object> equipment = new LinkedHashMap<>();

        if (errorTypes == null || errorTypes.isEmpty()) {
            return generateValidEquipment();
        }

        // name
        if (errorTypes.contains("EMPTY_NAME")) {
            equipment.put("name", "");
        } else {
            equipment.put("name", DEVICE_IDS[random.nextInt(DEVICE_IDS.length)]);
        }

        // file
        if (errorTypes.contains("EMPTY_FILE")) {
            equipment.put("file", "");
        } else if (errorTypes.contains("FILE_TOO_SHORT")) {
            equipment.put("file", "B8");  // Seulement 2 caractères au lieu de 3 minimum
        } else {
            equipment.put("file", FILE_IDS[random.nextInt(FILE_IDS.length)]);
        }

        // deployDateTime
        if (errorTypes.contains("EMPTY_DEPLOY_DATE")) {
            equipment.put("deployDateTime", "");
        } else if (errorTypes.contains("INVALID_DEPLOY_DATE")) {
            equipment.put("deployDateTime", generateInvalidDateTime());
        } else {
            equipment.put("deployDateTime", generateValidDateTime());
        }

        return equipment;
    }

    /**
     * Génère une date/heure valide
     */
    private String generateValidDateTime() {
        LocalDateTime now = LocalDateTime.now().minusDays(random.nextInt(365));
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    /**
     * Génère une date/heure invalide
     */
    private String generateInvalidDateTime() {
        int type = random.nextInt(4);
        return switch (type) {
            case 0 -> "2025-13-32T13:34:00.000Z";  // Mois et jour invalides
            case 1 -> "2025-12-32T13:34:00.000Z";  // Jour invalide
            case 2 -> "2025-12-12T25:34:00.000Z";  // Heure invalide
            case 3 -> "2025-12-12T13:61:00.000Z";  // Minutes invalides
            default -> "0000-00-00T00:00:00.000Z"; // Date complètement invalide
        };
    }

    /**
     * Génère une chaîne longue
     */
    private String generateLongString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('A' + random.nextInt(26)));
        }
        return sb.toString();
    }
}

