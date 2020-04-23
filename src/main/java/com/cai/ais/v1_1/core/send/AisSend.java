package com.cai.ais.v1_1.core.send;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AisSend {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send(String message, String exchangeName){
        rabbitTemplate.send(exchangeName, MessageBuilder.withBody(message.getBytes()).build());
    }
}
