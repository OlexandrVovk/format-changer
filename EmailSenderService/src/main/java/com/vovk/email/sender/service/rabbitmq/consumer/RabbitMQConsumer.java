package com.vovk.email.sender.service.rabbitmq.consumer;

import com.vovk.email.sender.service.email.services.EmailService;
import com.vovk.email.sender.service.rabbitmq.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final EmailService emailService;
    private final String DIR_PDF = "./src/main/resources/static/documents/pdf/";

    @SneakyThrows
    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(MessageDto messageDTO){
        File fileToSend = Files.write(Paths.get(DIR_PDF+messageDTO.getFileName()),messageDTO.getPdfFile()).toFile();
        emailService.sendEmailWithAttachment(
                messageDTO.getEmail(),
                "Converted PDF",
                messageDTO.getMessage(),
                fileToSend
        );
    }
}
