package com.cai.ais.core.send;

import com.cai.ais.AisMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AisSend {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void send(AisMessage message, String exchangeName){
        rabbitTemplate.convertAndSend(exchangeName, "",message);
    }

    public void send(AisMessage message, String exchangeName, String routeKey){
        rabbitTemplate.convertAndSend(exchangeName, routeKey, message);
    }
}
