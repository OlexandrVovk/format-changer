package com.vovk.email.sender.service.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDto {
    private String to;
    private String subject;
    private String body;
    private String attachmentFilePath;
}
