package com.bvv.filegeneration.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportFileDTO {

    private MultipartFile file;
    private String prefix;
}

