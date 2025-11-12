package com.bvv.filegeneration.mapper;

import com.bvv.filegeneration.dto.FieldDTO;
import com.bvv.filegeneration.entity.TemplateField;
import org.springframework.stereotype.Component;

@Component
public class FieldMapper {

    public FieldDTO toDTO(TemplateField field) {
        if (field == null) {
            return null;
        }

        return FieldDTO.builder()
                .id(field.getId())
                .fieldName(field.getFieldName())
                .fieldType(field.getFieldType())
                .required(field.getRequired())
                .minLength(field.getMinLength())
                .maxLength(field.getMaxLength())
                .minValue(field.getMinValue())
                .maxValue(field.getMaxValue())
                .dateFormat(field.getDateFormat())
                .possibleValues(field.getPossibleValues())
                .errorInjectionStrategy(field.getErrorInjectionStrategy())
                .position(field.getPosition())
                .build();
    }

    public TemplateField toEntity(FieldDTO dto) {
        if (dto == null) {
            return null;
        }

        return TemplateField.builder()
                .id(dto.getId())
                .fieldName(dto.getFieldName())
                .fieldType(dto.getFieldType())
                .required(dto.getRequired())
                .minLength(dto.getMinLength())
                .maxLength(dto.getMaxLength())
                .minValue(dto.getMinValue())
                .maxValue(dto.getMaxValue())
                .dateFormat(dto.getDateFormat())
                .possibleValues(dto.getPossibleValues())
                .errorInjectionStrategy(dto.getErrorInjectionStrategy())
                .position(dto.getPosition())
                .build();
    }
}

