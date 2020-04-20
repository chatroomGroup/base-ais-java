package com.cai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
public class ChatroomBusinessApplication{

    public static void main(String[] args) {
        SpringApplication.run(ChatroomBusinessApplication.class,args);
    }
}
