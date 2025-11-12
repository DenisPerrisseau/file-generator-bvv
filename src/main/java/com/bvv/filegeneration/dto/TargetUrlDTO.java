package com.bvv.filegeneration.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUrlDTO {
    private Long id;
    private String name;
    private String url;
    private String bearerToken;
    private String description;
    private Boolean active;
}

