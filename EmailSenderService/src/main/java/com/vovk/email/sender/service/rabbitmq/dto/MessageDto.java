package com.vovk.email.sender.service.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private String message;
    private String fileName;
    private String email;
    private byte[] pdfFile;
}
