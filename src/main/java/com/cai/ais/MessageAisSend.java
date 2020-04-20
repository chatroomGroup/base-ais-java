package com.cai.ais;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class MessageAisSend implements ApplicationContextAware,Serializable {
    ApplicationContext context;

    @Autowired
    MessageConsumerConfig consumer;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MessageConfig config;

    //Direct
    public void send(String message, String queue, TokenDomain session){
        AisService aisService = (AisService)context.getBean((String) consumer.getQueses().get(queue));
        AisMessage aisMessage = new AisMessage();
        aisMessage.setBody(message);

        rabbitTemplate.convertAndSend(queue,aisMessage,mes -> {
            config.setDelegate(aisService); //设置监听目标类
            mes.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
            mes.getMessageProperties().setHeader("test1","test1");
            mes.getMessageProperties().setHeader("test2","test2");
            mes.getMessageProperties().setHeader("test3","test3");
//            aisService.process(aisMessage)
            return mes;
        });

    }

    //Topic
    public void send(String exchange, String message, String queue){
        AisMessage aisMessage = new AisMessage();
        aisMessage.setBody(message);
        config.setListenerMethod();
        rabbitTemplate.convertAndSend(exchange,queue,aisMessage);
    }

    //Fanout
    public void send(String exchange, String message, List<String> queues){
        AisMessage aisMessage = new AisMessage();
        aisMessage.setBody(message);
        config.setListenerMethod();
        config.declareQueuesToExchange(exchange,queues);
        rabbitTemplate.convertAndSend(exchange,null,aisMessage);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
