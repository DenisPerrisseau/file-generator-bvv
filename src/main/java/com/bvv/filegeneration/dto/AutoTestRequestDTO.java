package com.bvv.filegeneration.dto;

import com.bvv.filegeneration.common.enums.ErrorType;
import com.bvv.filegeneration.common.enums.OutputFormat;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoTestRequestDTO {

    private Long templateId;
    private Integer totalLines;
    private Integer errorLines;
    private Integer duplicateLines;
    private OutputFormat outputFormat;
    private List<ErrorType> selectedErrorTypes;
    private Long targetUrlId;
    private String bearerToken;

    // Résultats attendus
    private Integer expectedLignesTraitees;
    private Integer expectedLignesInsert;
    private Integer expectedLignesUpdate;
    private Integer expectedLignesIgnorees;
    private Integer expectedHttpStatus;
}

