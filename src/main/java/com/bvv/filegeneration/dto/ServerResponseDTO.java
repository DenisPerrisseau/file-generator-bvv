package com.bvv.filegeneration.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerResponseDTO {
    private Integer lignesTraitees;
    private Integer lignesInsert;
    private Integer lignesUpdate;
    private Integer lignesIgnorees;
    private String messageAvertissement;
    private String statut; // PARTIAL, SUCCESS, ERROR
}

