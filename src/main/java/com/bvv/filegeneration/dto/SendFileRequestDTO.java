package com.bvv.filegeneration.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendFileRequestDTO {

    private Long jobId;
    private String url;
    private String secret;
}

