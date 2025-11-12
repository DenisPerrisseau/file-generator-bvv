package com.bvv.filegeneration.dto;

import com.bvv.filegeneration.common.enums.OutputFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateFileRequestDTO {

    @NotNull(message = "Template ID is required")
    private Long templateId;

    @NotNull(message = "Total lines is required")
    @Min(value = 1, message = "Total lines must be at least 1")
    private Integer totalLines;

    @NotNull(message = "Error lines is required")
    @Min(value = 0, message = "Error lines cannot be negative")
    private Integer errorLines;

    @NotNull(message = "Output format is required")
    private OutputFormat outputFormat;
}

