package com.bvv.filegeneration.dto;

import com.bvv.filegeneration.common.enums.JobStatus;
import com.bvv.filegeneration.common.enums.OutputFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationJobDTO {

    private Long id;
    private Long templateId;
    private Integer totalLines;
    private Integer errorLines;
    private OutputFormat outputFormat;
    private JobStatus status;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}

