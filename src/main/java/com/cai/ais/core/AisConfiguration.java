package com.cai.ais.core;

import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReplyTimeout(10000);
        template.setUseDirectReplyToContainer(false);
        return template;
    }
//
//    @Bean
//    AmqpAdmin amqpAdmin(@Qualifier("connectionFactory") ConnectionFactory connectionFactory){
//        return new RabbitAdmin(connectionFactory);
//    }

    @Bean
    public DirectExchange ex() {
        return new DirectExchange("ex");
    }

    @Bean
    MessageConverter messageConverter(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter(objectMapper);
        messageConverter.setClassMapper(new ClassMapper() {
            @Override
            public void fromClass(Class<?> clazz, MessageProperties properties) {
            }

            @Override
            public Class<?> toClass(MessageProperties properties) {
                return AisMessage.class;
            }
        });
        return messageConverter;
    }


}
