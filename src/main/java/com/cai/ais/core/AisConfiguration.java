package com.cai.ais.core;

import com.cai.ais.AisProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AisConfiguration {

    @Autowired
    ApplicationContext context;

    @Autowired
    AisProperties aisProperties;

    @Bean
    ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(aisProperties.getUsername());
        connectionFactory.setPassword(aisProperties.getPassword());
        connectionFactory.setVirtualHost(aisProperties.getVirtualHost());
        connectionFactory.setHost(aisProperties.getHost());
        connectionFactory.setPort(aisProperties.getPort());
        return connectionFactory;
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        return new RabbitTemplate(connectionFactory);
    }
//
//    @Bean
//    AmqpAdmin amqpAdmin(@Qualifier("connectionFactory") ConnectionFactory connectionFactory){
//        return new RabbitAdmin(connectionFactory);
//    }

    @Bean
    MessageConverter messageConverter(){
        return new SimpleMessageConverter();
    }


}