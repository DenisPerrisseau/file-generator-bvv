package com.bvv.filegeneration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service de génération de fichiers JSON multi-clés (ACQ PE)
 */
@Service
@Slf4j
public class MultiKeysJsonGeneratorService {

    private final Random random = new Random();

    private static final String[] GROUP_NAMES = {
            "PNO309", "PNO385", "PNO456", "PNO567", "PNO678", "PNO789"
    };

    private static final String[] FILE_NAMES = {
            "paramtt11", "paramtt12", "paramtt13", "paramtt14", "paramtt17", "paramtt18"
    };

    /**
     * Génère un fichier JSON multi-clés avec configuration des erreurs et doublons
     */
    public String generateMultiKeysJson(int totalLines, int errorLines, List<String> errorTypes, int duplicateLines) {
        try {
            Map<String, Object> root = new LinkedHashMap<>();

            // Déterminer le nombre de groupes et lignes par groupe
            int numberOfGroups = Math.max(2, totalLines / 3);
            int linesPerGroup = totalLines / numberOfGroups;

            List<Map<String, Object>> validLines = new ArrayList<>();

            // Générer les lignes valides
            for (int g = 0; g < numberOfGroups; g++) {
                String groupName = GROUP_NAMES[g % GROUP_NAMES.length];
                List<Map<String, Object>> groupLines = new ArrayList<>();

                for (int i = 0; i < linesPerGroup; i++) {
                    Map<String, Object> line = generateValidParameterLine();
                    groupLines.add(line);
                    validLines.add(line);
                }

                root.put(groupName, groupLines);
            }

            // Ajouter les lignes avec erreurs
            int errorCount = 0;
            for (String groupKey : root.keySet()) {
                if (errorCount >= errorLines) break;

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> groupList = (List<Map<String, Object>>) root.get(groupKey);

                if (!groupList.isEmpty()) {
                    int errorIndex = random.nextInt(groupList.size());
                    groupList.set(errorIndex, generateParameterLineWithErrors(errorTypes));
                    errorCount++;
                }
            }

            // Ajouter les doublons
            if (duplicateLines > 0 && !validLines.isEmpty()) {
                String duplicateGroupName = GROUP_NAMES[random.nextInt(numberOfGroups) % GROUP_NAMES.length];
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> duplicateGroup = (List<Map<String, Object>>) root.get(duplicateGroupName);

                for (int i = 0; i < duplicateLines; i++) {
                    int indexToDuplicate = random.nextInt(validLines.size());
                    Map<String, Object> duplicate = new LinkedHashMap<>(validLines.get(indexToDuplicate));
                    duplicateGroup.add(duplicate);
                }
            }

            return formatJsonPretty(root);

        } catch (Exception e) {
            log.error("Erreur génération multi-clés JSON", e);
            throw new RuntimeException("Erreur génération multi-clés JSON: " + e.getMessage());
        }
    }

    /**
     * Génère une ligne de paramètre valide
     */
    private Map<String, Object> generateValidParameterLine() {
        Map<String, Object> line = new LinkedHashMap<>();

        line.put("deployDateTime", generateValidDateTime());
        line.put("fetchDateTime", generateValidDateTime());
        line.put("file", FILE_NAMES[random.nextInt(FILE_NAMES.length)]);
        line.put("version", String.format("%04d", 7000 + random.nextInt(3000)));

        return line;
    }

    /**
     * Génère une ligne de paramètre avec erreurs spécifiques à ACQ PE
     */
    private Map<String, Object> generateParameterLineWithErrors(List<String> errorTypes) {
        Map<String, Object> line = new LinkedHashMap<>();

        boolean hasError = errorTypes != null && !errorTypes.isEmpty();

        // deployDateTime
        line.put("deployDateTime", generateValidDateTime());

        // fetchDateTime
        if (hasError && errorTypes.contains("EMPTY_FETCH_DATE")) {
            line.put("fetchDateTime", "");
        } else if (hasError && errorTypes.contains("INVALID_FETCH_DATE")) {
            line.put("fetchDateTime", "2025-13-32T25:61:00.074Z");
        } else {
            line.put("fetchDateTime", generateValidDateTime());
        }

        // file
        if (hasError && errorTypes.contains("EMPTY_FILE_PE")) {
            line.put("file", "");
        } else {
            line.put("file", FILE_NAMES[random.nextInt(FILE_NAMES.length)]);
        }

        // version
        if (hasError && errorTypes.contains("EMPTY_VERSION")) {
            line.put("version", "");
        } else if (hasError && errorTypes.contains("VERSION_WRONG_LENGTH")) {
            // Générer une version qui n'a pas exactement 3 chiffres
            int wrongLength = random.nextBoolean() ? random.nextInt(10) : 1000 + random.nextInt(1000);
            line.put("version", String.valueOf(wrongLength));
        } else {
            line.put("version", String.format("%03d", 100 + random.nextInt(900)));
        }

        return line;
    }

    /**
     * Génère une date/heure valide
     */
    private String generateValidDateTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now().minusDays(random.nextInt(365));
        return now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    /**
     * Formate le JSON en pretty print
     */
    private String formatJsonPretty(Map<String, Object> data) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        int index = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            json.append("    \"").append(entry.getKey()).append("\": ");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) entry.getValue();
            json.append("[\n");

            for (int i = 0; i < list.size(); i++) {
                json.append("        {\n");
                Map<String, Object> item = list.get(i);
                List<String> keys = new ArrayList<>(item.keySet());

                for (int j = 0; j < keys.size(); j++) {
                    String key = keys.get(j);
                    Object value = item.get(key);
                    json.append("            \"").append(key).append("\": ");

                    if (value instanceof String) {
                        json.append("\"").append(value).append("\"");
                    } else {
                        json.append(value);
                    }

                    if (j < keys.size() - 1) {
                        json.append(",");
                    }
                    json.append("\n");
                }

                json.append("        }");
                if (i < list.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }

            json.append("    ]");
            if (index < data.size() - 1) {
                json.append(",");
            }
            json.append("\n");

            index++;
        }

        json.append("}");
        return json.toString();
    }
}

