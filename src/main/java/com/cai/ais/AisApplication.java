package com.cai.ais;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
public class AisApplication{

    public static void main(String[] args) {
        SpringApplication.run(AisApplication.class,args);
    }
}