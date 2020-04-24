package com.cai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
public class Application{

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
