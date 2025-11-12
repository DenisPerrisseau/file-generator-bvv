package com.bvv.filegeneration.dto;

import com.bvv.filegeneration.common.enums.ActionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationLogDTO {

    private Long id;
    private Long jobId;
    private ActionType action;
    private Integer httpStatus;
    private String responseMessage;
    private LocalDateTime sentAt;
    private Integer retryCount;
    private LocalDateTime createdAt;
}

