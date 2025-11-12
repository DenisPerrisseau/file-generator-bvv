package com.bvv.filegeneration.service;

import com.bvv.filegeneration.common.enums.FieldType;
import com.bvv.filegeneration.common.enums.TemplateType;
import com.bvv.filegeneration.common.exceptions.FileGenerationException;
import com.bvv.filegeneration.dto.TemplateDTO;
import com.bvv.filegeneration.entity.Template;
import com.bvv.filegeneration.entity.TemplateField;
import com.bvv.filegeneration.utils.FileParser;
import com.bvv.filegeneration.utils.TemplateNamingRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TemplateImportService {

    private final TemplateService templateService;

    public TemplateImportService(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Importe un fichier et crée un template automatiquement
     */
    public TemplateDTO importFile(MultipartFile file, String prefix) {
        if (file.isEmpty()) {
            throw new FileGenerationException("Le fichier est vide");
        }

        try {
            // Lire le contenu du fichier
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            // Nettoyer le contenu (enlever BOM si présent)
            content = content.trim();
            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }

            // Déterminer le type de fichier
            TemplateType type = detectFileType(file.getOriginalFilename());

            // Générer le préfixe si non fourni
            if (prefix == null || prefix.trim().isEmpty()) {
                prefix = extractPrefixFromFilename(file.getOriginalFilename());
            }

            // Nettoyer le préfixe
            prefix = prefix.trim().toUpperCase();

            // Parser le fichier pour extraire les champs
            FileParser.ParsedFileData parsedData = FileParser.parseFile(content, type);

            // Vérifier qu'on a des champs
            if (parsedData.getFields() == null || parsedData.getFields().isEmpty()) {
                throw new FileGenerationException("Aucun champ détecté dans le fichier");
            }

            // Créer les champs du template
            List<TemplateField> fields = createFieldsFromParsedData(parsedData);

            // Générer le nom du template
            String templateName = prefix + "_TEMPLATE";

            // Créer le template
            return templateService.createTemplate(templateName, prefix, type, fields);

        } catch (FileGenerationException e) {
            throw e;
        } catch (IOException e) {
            throw new FileGenerationException("Erreur lors de la lecture du fichier: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new FileGenerationException("Erreur lors de l'import: " + e.getMessage(), e);
        }
    }

    /**
     * Détecte le type de fichier à partir de son nom
     */
    private TemplateType detectFileType(String filename) {
        if (filename == null) {
            throw new FileGenerationException("Nom de fichier invalide");
        }

        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".json")) {
            return TemplateType.JSON;
        } else if (lowerFilename.endsWith(".xml")) {
            return TemplateType.XML;
        } else if (lowerFilename.endsWith(".txt") || lowerFilename.endsWith(".csv")) {
            return TemplateType.TXT;
        } else {
            // Par défaut, essayer JSON
            return TemplateType.JSON;
        }
    }

    /**
     * Extrait un préfixe du nom de fichier
     */
    private String extractPrefixFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "DATA";
        }

        // Enlever l'extension
        String nameWithoutExt = filename.replaceAll("\\.[^.]*$", "");

        // Remplacer les caractères non alphanumériques par _
        String prefix = nameWithoutExt.toUpperCase()
                .replaceAll("[^A-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        if (prefix.isEmpty()) {
            return "DATA";
        }

        // Limiter à 20 caractères
        if (prefix.length() > 20) {
            prefix = prefix.substring(0, 20);
        }

        return prefix;
    }

    /**
     * Crée les champs du template à partir des données parsées
     */
    private List<TemplateField> createFieldsFromParsedData(FileParser.ParsedFileData parsedData) {
        List<TemplateField> fields = new ArrayList<>();

        int position = 1;
        for (FileParser.FieldInfo fieldInfo : parsedData.getFields()) {
            TemplateField field = TemplateField.builder()
                    .fieldName(fieldInfo.getFieldName())
                    .fieldType(mapDetectedTypeToFieldType(fieldInfo.getDetectedType()))
                    .required(true) // Par défaut, tous les champs sont requis
                    .position(position++)
                    .build();

            // Ajouter des contraintes par défaut selon le type
            switch (field.getFieldType()) {
                case STRING:
                    field.setMinLength(1);
                    field.setMaxLength(255);
                    break;
                case INTEGER:
                    field.setMinValue(new java.math.BigDecimal(0));
                    field.setMaxValue(new java.math.BigDecimal(999999));
                    break;
                case DECIMAL:
                    field.setMinValue(new java.math.BigDecimal(0));
                    field.setMaxValue(new java.math.BigDecimal(999999.99));
                    break;
                case DATE:
                    field.setDateFormat("yyyy-MM-dd");
                    break;
                default:
                    break;
            }

            fields.add(field);
        }

        return fields;
    }

    /**
     * Mappe le type détecté vers FieldType
     */
    private FieldType mapDetectedTypeToFieldType(String detectedType) {
        if (detectedType == null) {
            return FieldType.STRING;
        }

        switch (detectedType.toUpperCase()) {
            case "STRING":
                return FieldType.STRING;
            case "DECIMAL":
            case "NUMBER":
                return FieldType.DECIMAL;
            case "INTEGER":
            case "INT":
                return FieldType.INTEGER;
            case "BOOLEAN":
            case "BOOL":
                return FieldType.BOOLEAN;
            case "DATE":
                return FieldType.DATE;
            default:
                return FieldType.STRING;
        }
    }
}

