package com.cai.ais.config;

import com.cai.ais.config.AisProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    @Autowired
    ApplicationContext context;

    @Autowired
    AisProperties aisProperties;

    @Bean(name = "rabbitAdmin")
    RabbitAdmin rabbitAdmin(@Qualifier("connectionFactory") ConnectionFactory myConnectionFactory){
        return new RabbitAdmin(myConnectionFactory);
    }

}
