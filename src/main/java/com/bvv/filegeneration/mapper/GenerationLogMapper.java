package com.bvv.filegeneration.mapper;

import com.bvv.filegeneration.dto.GenerationLogDTO;
import com.bvv.filegeneration.entity.GenerationLog;
import org.springframework.stereotype.Component;

@Component
public class GenerationLogMapper {

    public GenerationLogDTO toDTO(GenerationLog log) {
        if (log == null) {
            return null;
        }

        return GenerationLogDTO.builder()
                .id(log.getId())
                .jobId(log.getJob().getId())
                .action(log.getAction())
                .httpStatus(log.getHttpStatus())
                .responseMessage(log.getResponseMessage())
                .sentAt(log.getSentAt())
                .retryCount(log.getRetryCount())
                .createdAt(log.getCreatedAt())
                .build();
    }

    public GenerationLog toEntity(GenerationLogDTO dto) {
        if (dto == null) {
            return null;
        }

        return GenerationLog.builder()
                .id(dto.getId())
                .action(dto.getAction())
                .httpStatus(dto.getHttpStatus())
                .responseMessage(dto.getResponseMessage())
                .sentAt(dto.getSentAt())
                .retryCount(dto.getRetryCount())
                .build();
    }
}

