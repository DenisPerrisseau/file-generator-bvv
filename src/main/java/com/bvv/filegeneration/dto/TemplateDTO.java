package com.bvv.filegeneration.dto;

import com.bvv.filegeneration.common.enums.TemplateType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateDTO {

    private Long id;
    private String name;
    private String prefix;
    private TemplateType type;
    private Integer version;
    private String nomenclature;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FieldDTO> fields;
}

