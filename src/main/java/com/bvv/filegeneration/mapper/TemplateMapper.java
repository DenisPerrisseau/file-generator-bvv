package com.bvv.filegeneration.mapper;

import com.bvv.filegeneration.dto.TemplateDTO;
import com.bvv.filegeneration.entity.Template;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TemplateMapper {

    private final FieldMapper fieldMapper;

    public TemplateMapper(FieldMapper fieldMapper) {
        this.fieldMapper = fieldMapper;
    }

    public TemplateDTO toDTO(Template template) {
        if (template == null) {
            return null;
        }

        return TemplateDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .prefix(template.getPrefix())
                .type(template.getType())
                .version(template.getVersion())
                .nomenclature(template.getNomenclature())
                .filePath(template.getFilePath())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .fields(template.getFields().stream()
                        .map(fieldMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public Template toEntity(TemplateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Template.builder()
                .id(dto.getId())
                .name(dto.getName())
                .prefix(dto.getPrefix())
                .type(dto.getType())
                .version(dto.getVersion())
                .nomenclature(dto.getNomenclature())
                .filePath(dto.getFilePath())
                .build();
    }
}

