package com.changer.view.controllers;

import com.changer.view.controllers.rabbit.dto.MessageDto;
import com.changer.view.controllers.rabbit.publisher.RabbitMQProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final RabbitMQProducer rabbitMQProducer;
    private final String PDF_FOLDER = "/app/src/main/resources/static/documents/pdf/";

    @SneakyThrows
    @GetMapping("/sendToEmail")
    public String sendToEmail(HttpServletRequest request, String email){
        System.out.println(email);
        String fileName = request.getSession().getAttribute("fileName").toString();
        File fileToSend = new File(PDF_FOLDER + fileName);
        rabbitMQProducer.sendMessage(MessageDto.builder()
                        .message("Here's your PDF file")
                        .fileName(fileName)
                        .email(email)
                        .pdfFile(Files.readAllBytes(Paths.get(PDF_FOLDER + fileName)))
                .build());
        return "redirect:/";
    }
}
