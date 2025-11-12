package com.bvv.filegeneration.mapper;

import com.bvv.filegeneration.dto.GenerationJobDTO;
import com.bvv.filegeneration.entity.GenerationJob;
import org.springframework.stereotype.Component;

@Component
public class GenerationJobMapper {

    public GenerationJobDTO toDTO(GenerationJob job) {
        if (job == null) {
            return null;
        }

        return GenerationJobDTO.builder()
                .id(job.getId())
                .templateId(job.getTemplate().getId())
                .totalLines(job.getTotalLines())
                .errorLines(job.getErrorLines())
                .outputFormat(job.getOutputFormat())
                .status(job.getStatus())
                .filePath(job.getFilePath())
                .createdAt(job.getCreatedAt())
                .completedAt(job.getCompletedAt())
                .build();
    }

    public GenerationJob toEntity(GenerationJobDTO dto) {
        if (dto == null) {
            return null;
        }

        return GenerationJob.builder()
                .id(dto.getId())
                .totalLines(dto.getTotalLines())
                .errorLines(dto.getErrorLines())
                .outputFormat(dto.getOutputFormat())
                .status(dto.getStatus())
                .filePath(dto.getFilePath())
                .build();
    }
}

