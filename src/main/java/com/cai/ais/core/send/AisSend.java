package com.cai.ais.core.send;

import com.cai.ais.AisMessage;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

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

    public Object sendAndReceive(AisMessage message, String exchangeName){
        return rabbitTemplate.convertSendAndReceive(exchangeName
                , ""
                , message
                , message1 -> {
//                    message1.getMessageProperties().setReplyTo("amq.rabbitmq.reply-to");
                    message1.getMessageProperties().setCorrelationId(UUID.randomUUID().toString()
                    );
                    return message1;
                }
        );
    }
}
