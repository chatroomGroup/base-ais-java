package com.cai.ais.v1_1.core;

import com.cai.ais.AisProperties;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Bean
    AmqpAdmin amqpAdmin(@Qualifier("connectionFactory") ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }



}
