package com.bvv.filegeneration.dto;

import com.bvv.filegeneration.common.enums.OutputFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateFileRequestDTO {

    @NotNull(message = "Template ID is required")
    private Long templateId;

    @NotNull(message = "Total lines is required")
    @Min(value = 1, message = "Total lines must be at least 1")
    private Integer totalLines;

    @NotNull(message = "Error lines is required")
    @Min(value = 0, message = "Error lines cannot be negative")
    private Integer errorLines;

    @Min(value = 0, message = "Duplicate lines cannot be negative")
    @Min(value = 0, message = "Duplicate lines cannot be negative")
    private Integer duplicateLines;

    private String selectedErrorTypes; // Types d'erreurs sélectionnés séparés par virgule (ex: "NULL_VALUE,INVALID_DATE")

    @NotNull(message = "Output format is required")
    private OutputFormat outputFormat;

    private Long targetUrlId; // ID de l'URL de destination (optionnel pour génération simple)

    // Valeurs attendues pour la comparaison (optionnelles)
    private Integer expectedLinesTreated;
    private Integer expectedLinesInsert;
    private Integer expectedLinesUpdate;
    private Integer expectedLinesIgnored;
    private Integer expectedHttpStatus;
}

