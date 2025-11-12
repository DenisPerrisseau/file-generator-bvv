package com.bvv.filegeneration.utils;

import com.bvv.filegeneration.common.enums.TemplateType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TemplateNamingRules {

    private TemplateNamingRules() {
    }

    /**
     * Génère la nomenclature standardisée pour un gabarit
     * Format: PREFIX_TYPE_yyyyMMdd_vN.json
     * Exemple: CLIENTS_JSON_20251112_v1.json
     */
    public static String generateNomenclature(String prefix, TemplateType type, Integer version) {
        if (prefix == null || prefix.trim().isEmpty() || type == null || version == null) {
            throw new IllegalArgumentException("Prefix, type, and version cannot be null");
        }

        LocalDate today = LocalDate.now();
        String date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return String.format("%s_%s_%s_v%d.json", prefix.toUpperCase(), type.name(), date, version);
    }

    /**
     * Génère le chemin de fichier pour un gabarit
     */
    public static String generateTemplateFilePath(String prefix, TemplateType type, Integer version) {
        String nomenclature = generateNomenclature(prefix, type, version);
        return "files/templates/" + nomenclature;
    }

    /**
     * Génère le chemin de fichier pour les données générées
     */
    public static String generateDataFilePath(String prefix, TemplateType type, Integer version, Long jobId) {
        LocalDate today = LocalDate.now();
        String date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = String.format("%s_%s_%s_v%d_data_%d.json",
                prefix.toUpperCase(), type.name(), date, version, jobId);
        return "files/generated/" + filename;
    }
}

