
package com.bvv.filegeneration.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparisonResultDTO {
    private String field; // Nom du champ (ex: "lignesTraitees", "httpStatus")
    private Object expected; // Valeur attendue
    private Object actual; // Valeur réelle
    private Boolean matches; // true = ✓, false = ✗
    private String message; // Message explicatif
}

