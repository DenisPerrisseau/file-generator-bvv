package com.bvv.filegeneration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bvv.filegeneration.common.enums.TemplateType;
import com.bvv.filegeneration.common.exceptions.FileGenerationException;

import java.util.ArrayList;
import java.util.List;

public class FileParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private FileParser() {
    }

    /**
     * Analyse un fichier et détecte son type et structure
     */
    public static ParsedFileData parseFile(String content, TemplateType type) {
        try {
            switch (type) {
                case JSON:
                    return parseJson(content);
                case XML:
                    return parseXml(content);
                case TXT:
                    return parseTxt(content);
                default:
                    throw new FileGenerationException("Type de fichier non supporté: " + type);
            }
        } catch (Exception e) {
            throw new FileGenerationException("Erreur lors de l'analyse du fichier: " + e.getMessage(), e);
        }
    }

    private static ParsedFileData parseJson(String content) {
        try {
            JsonNode root = objectMapper.readTree(content);
            ParsedFileData data = new ParsedFileData();
            data.setType(TemplateType.JSON);

            if (root.isArray()) {
                if (root.size() > 0) {
                    JsonNode firstItem = root.get(0);
                    if (firstItem != null && firstItem.isObject()) {
                        extractFields(firstItem, data);
                    } else {
                        throw new FileGenerationException("Le premier élément du tableau JSON n'est pas un objet valide");
                    }
                } else {
                    throw new FileGenerationException("Le tableau JSON est vide, impossible d'extraire la structure");
                }
            } else if (root.isObject()) {
                extractFields(root, data);
            } else {
                throw new FileGenerationException("Le JSON doit être un objet ou un tableau d'objets");
            }

            return data;
        } catch (JsonProcessingException e) {
            throw new FileGenerationException(
                "Format JSON invalide. Vérifiez qu'il n'y a pas de virgule en trop, " +
                "d'accolade ou de crochet manquant. Ligne " + e.getLocation().getLineNr() +
                ", colonne " + e.getLocation().getColumnNr(), e);
        }
    }

    private static ParsedFileData parseXml(String content) {
        // À implémenter selon les besoins
        ParsedFileData data = new ParsedFileData();
        data.setType(TemplateType.XML);
        return data;
    }

    private static ParsedFileData parseTxt(String content) {
        // À implémenter selon les besoins
        ParsedFileData data = new ParsedFileData();
        data.setType(TemplateType.TXT);
        return data;
    }

    private static void extractFields(JsonNode node, ParsedFileData data) {
        node.fieldNames().forEachRemaining(fieldName -> {
            JsonNode fieldNode = node.get(fieldName);
            FieldInfo field = new FieldInfo();
            field.setFieldName(fieldName);

            if (fieldNode.isTextual()) {
                field.setDetectedType("STRING");
            } else if (fieldNode.isNumber()) {
                // Différencier INTEGER et DECIMAL
                if (fieldNode.isInt() || fieldNode.isLong()) {
                    field.setDetectedType("INTEGER");
                } else {
                    field.setDetectedType("DECIMAL");
                }
            } else if (fieldNode.isBoolean()) {
                field.setDetectedType("BOOLEAN");
            } else if (fieldNode.isArray()) {
                field.setDetectedType("ARRAY");
            } else if (fieldNode.isObject()) {
                field.setDetectedType("OBJECT");
            } else {
                field.setDetectedType("STRING");
            }

            data.getFields().add(field);
        });
    }

    // Classes internes pour les résultats du parsing
    public static class ParsedFileData {
        private TemplateType type;
        private List<FieldInfo> fields = new ArrayList<>();

        public TemplateType getType() {
            return type;
        }

        public void setType(TemplateType type) {
            this.type = type;
        }

        public List<FieldInfo> getFields() {
            return fields;
        }

        public void setFields(List<FieldInfo> fields) {
            this.fields = fields;
        }
    }

    public static class FieldInfo {
        private String fieldName;
        private String detectedType;
        private Integer position;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getDetectedType() {
            return detectedType;
        }

        public void setDetectedType(String detectedType) {
            this.detectedType = detectedType;
        }

        public Integer getPosition() {
            return position;
        }

        public void setPosition(Integer position) {
            this.position = position;
        }
    }
}

