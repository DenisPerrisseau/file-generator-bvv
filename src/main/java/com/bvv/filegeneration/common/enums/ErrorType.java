package com.bvv.filegeneration.common.enums;

public enum ErrorType {
    NULL_VALUE("Valeur NULL", "Champ obligatoire laissé vide (null)"),
    EMPTY_STRING("Chaîne Vide", "Champ texte vide (\"\")"),
    INVALID_DATE("Date Invalide", "Format de date incorrect (ex: 2025-13-45)"),
    OUT_OF_BOUNDS("Hors Limites", "Valeur numérique hors min/max"),
    WRONG_TYPE("Type Incorrect", "Type de donnée incorrect (texte au lieu de nombre)"),
    TOO_LONG("Trop Long", "Longueur maximale dépassée"),
    TOO_SHORT("Trop Court", "Longueur minimale non atteinte"),
    INVALID_FORMAT("Format Invalide", "Format de données incorrect (email, téléphone, etc.)"),
    DUPLICATE("Doublon", "Ligne dupliquée dans le fichier"),
    MISSING_REQUIRED("Champ Manquant", "Champ obligatoire absent");

    private final String label;
    private final String description;

    ErrorType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}

