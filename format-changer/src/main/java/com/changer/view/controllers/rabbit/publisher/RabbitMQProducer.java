package com.changer.view.controllers.rabbit.publisher;

import com.changer.view.controllers.rabbit.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate template;
    public void sendMessage(MessageDto message){
        template.convertAndSend(exchange, routingKey, message);
    }
}
