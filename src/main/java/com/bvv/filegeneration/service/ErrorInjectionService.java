package com.bvv.filegeneration.service;

import com.bvv.filegeneration.common.enums.ErrorType;
import com.bvv.filegeneration.common.enums.FieldType;
import com.bvv.filegeneration.entity.TemplateField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service pour générer des données avec des types d'erreurs spécifiques
 */
@Service
@Slf4j
public class ErrorInjectionService {

    private final Random random = new Random();

    /**
     * Génère une valeur avec le type d'erreur spécifié
     */
    public Object generateValueWithError(TemplateField field, ErrorType errorType) {
        switch (errorType) {
            case NULL_VALUE:
                return null;

            case EMPTY_STRING:
                return field.getFieldType() == FieldType.STRING ? "" : generateValidValue(field);

            case INVALID_DATE:
                return field.getFieldType() == FieldType.DATE ? "2025-13-45" : generateValidValue(field);

            case OUT_OF_BOUNDS:
                return generateOutOfBoundsValue(field);

            case WRONG_TYPE:
                return generateWrongType(field);

            case TOO_LONG:
                return generateTooLong(field);

            case TOO_SHORT:
                return generateTooShort(field);

            case INVALID_FORMAT:
                return generateInvalidFormat(field);

            case MISSING_REQUIRED:
                return null; // Même que NULL_VALUE

            default:
                log.warn("Type d'erreur non géré: {}", errorType);
                return generateValidValue(field);
        }
    }

    /**
     * Génère une valeur valide
     */
    public Object generateValidValue(TemplateField field) {
        switch (field.getFieldType()) {
            case STRING:
                return generateValidString(field);
            case INTEGER:
                return generateValidInteger(field);
            case DECIMAL:
                return generateValidDecimal(field);
            case BOOLEAN:
                return random.nextBoolean();
            case DATE:
                return generateValidDate(field);
            default:
                return "value_" + random.nextInt(1000);
        }
    }

    private String generateValidString(TemplateField field) {
        int minLength = field.getMinLength() != null ? field.getMinLength() : 1;
        int maxLength = field.getMaxLength() != null ? field.getMaxLength() : 50;

        // Générer une longueur valide
        int length = minLength + random.nextInt(Math.max(1, maxLength - minLength + 1));
        length = Math.min(length, maxLength);

        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private Integer generateValidInteger(TemplateField field) {
        int min = field.getMinValue() != null ? field.getMinValue().intValue() : 0;
        int max = field.getMaxValue() != null ? field.getMaxValue().intValue() : 10000;

        if (max <= min) {
            return min;
        }

        return min + random.nextInt(max - min + 1);
    }

    private Double generateValidDecimal(TemplateField field) {
        double min = field.getMinValue() != null ? field.getMinValue().doubleValue() : 0.0;
        double max = field.getMaxValue() != null ? field.getMaxValue().doubleValue() : 10000.0;
        return Math.round((min + (max - min) * random.nextDouble()) * 100.0) / 100.0;
    }

    private String generateValidDate(TemplateField field) {
        String format = field.getDateFormat() != null ? field.getDateFormat() : "yyyy-MM-dd";
        LocalDateTime date = LocalDateTime.now().minusDays(random.nextInt(365));
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    private Object generateOutOfBoundsValue(TemplateField field) {
        switch (field.getFieldType()) {
            case INTEGER:
                if (field.getMaxValue() != null) {
                    return field.getMaxValue().intValue() + 1000;
                } else if (field.getMinValue() != null) {
                    return field.getMinValue().intValue() - 1000;
                }
                return 999999999;

            case DECIMAL:
                if (field.getMaxValue() != null) {
                    return field.getMaxValue().doubleValue() + 1000.0;
                }
                return 999999999.99;

            case STRING:
                if (field.getMaxLength() != null) {
                    return "X".repeat(field.getMaxLength() + 10);
                }
                return "X".repeat(1000);

            default:
                return generateValidValue(field);
        }
    }

    private Object generateWrongType(TemplateField field) {
        switch (field.getFieldType()) {
            case INTEGER:
                return "NOT_A_NUMBER";
            case DECIMAL:
                return "INVALID_DECIMAL";
            case DATE:
                return "NOT_A_DATE";
            case BOOLEAN:
                return "MAYBE";
            default:
                return 12345; // Nombre au lieu de string
        }
    }

    private Object generateTooLong(TemplateField field) {
        if (field.getFieldType() == FieldType.STRING && field.getMaxLength() != null) {
            int tooLong = field.getMaxLength() + random.nextInt(20) + 5;
            return "X".repeat(tooLong);
        }
        return generateOutOfBoundsValue(field);
    }

    private Object generateTooShort(TemplateField field) {
        if (field.getFieldType() == FieldType.STRING && field.getMinLength() != null && field.getMinLength() > 0) {
            int tooShort = Math.max(0, field.getMinLength() - random.nextInt(field.getMinLength()) - 1);
            return tooShort > 0 ? "X".repeat(tooShort) : "";
        }
        return "";
    }

    private Object generateInvalidFormat(TemplateField field) {
        switch (field.getFieldType()) {
            case DATE:
                // Dates invalides variées
                String[] invalidDates = {
                    "2025-13-45",  // Mois et jour invalides
                    "2025-02-30",  // Février avec 30 jours
                    "9999-99-99",  // Complètement invalide
                    "01/32/2025",  // Format différent + jour invalide
                    "2025-00-00"   // Zeros
                };
                return invalidDates[random.nextInt(invalidDates.length)];

            case STRING:
                // Si c'est supposé être un email, téléphone, etc.
                String fieldName = field.getFieldName().toLowerCase();
                if (fieldName.contains("email")) {
                    return "invalid.email.format";
                } else if (fieldName.contains("phone") || fieldName.contains("tel")) {
                    return "123-ABC-DEFG";
                }
                return "Invalid@#$%Format";

            default:
                return generateWrongType(field);
        }
    }

    /**
     * Parse les types d'erreurs depuis une chaîne (ex: "NULL_VALUE,INVALID_DATE")
     */
    public Set<ErrorType> parseErrorTypes(String errorTypesStr) {
        Set<ErrorType> errorTypes = new HashSet<>();

        if (errorTypesStr == null || errorTypesStr.trim().isEmpty()) {
            return errorTypes;
        }

        String[] types = errorTypesStr.split(",");
        for (String type : types) {
            try {
                errorTypes.add(ErrorType.valueOf(type.trim()));
            } catch (IllegalArgumentException e) {
                log.warn("Type d'erreur inconnu: {}", type);
            }
        }

        return errorTypes;
    }

    /**
     * Sélectionne un type d'erreur aléatoire parmi ceux configurés
     */
    public ErrorType selectRandomErrorType(Set<ErrorType> configuredErrors) {
        if (configuredErrors.isEmpty()) {
            // Si aucun type d'erreur configuré, utiliser un type aléatoire
            ErrorType[] allTypes = ErrorType.values();
            return allTypes[random.nextInt(allTypes.length)];
        }

        List<ErrorType> errorList = new ArrayList<>(configuredErrors);
        return errorList.get(random.nextInt(errorList.size()));
    }
}

