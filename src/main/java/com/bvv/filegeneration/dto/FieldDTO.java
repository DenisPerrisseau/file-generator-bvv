package com.bvv.filegeneration.dto;

import com.bvv.filegeneration.common.enums.FieldType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDTO {

    private Long id;
    private String fieldName;
    private FieldType fieldType;
    private Boolean required;
    private Integer minLength;
    private Integer maxLength;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String dateFormat;
    private String possibleValues;
    private String errorInjectionStrategy;
    private Integer position;
}

