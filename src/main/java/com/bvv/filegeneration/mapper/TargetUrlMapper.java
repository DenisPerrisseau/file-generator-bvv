package com.bvv.filegeneration.mapper;

import com.bvv.filegeneration.dto.TargetUrlDTO;
import com.bvv.filegeneration.entity.TargetUrl;
import org.springframework.stereotype.Component;

@Component
public class TargetUrlMapper {

    public TargetUrlDTO toDTO(TargetUrl entity) {
        if (entity == null) {
            return null;
        }

        return TargetUrlDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .url(entity.getUrl())
                .bearerToken(entity.getBearerToken())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }

    public TargetUrl toEntity(TargetUrlDTO dto) {
        if (dto == null) {
            return null;
        }

        return TargetUrl.builder()
                .id(dto.getId())
                .name(dto.getName())
                .url(dto.getUrl())
                .bearerToken(dto.getBearerToken())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }
}

