package com.vovk.email.sender.service.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class EmailConfig {

    @Bean
    public SimpleMailMessage emailTemplate(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("olexandr.wowk@gmail.com");
        return message;
    }
}
