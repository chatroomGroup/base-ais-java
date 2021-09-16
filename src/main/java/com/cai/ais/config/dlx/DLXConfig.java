package com.cai.ais.config.dlx;

import com.cai.ais.config.AisMessage;
import com.cai.ais.config.ConsumerProcessProxy;
import com.cai.ais.core.exception.AisException;
import com.rabbitmq.client.*;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.cai.ais.utils.ConvertUtil.*;

/**
 * 死信队列的配置
 */
@Configuration
public class DLXConfig {

    private static final String queueN = "dlx.queue";

    private static final String exchangeN = "dlx.exchange";

    private final Integer channelCount = 10;

    private Connection connection;

    private Channel channel;

    private List<Channel> channels = new ArrayList<>();

    @Autowired
    ConnectionFactory connectionFactory;


    @Bean
    @ConditionalOnMissingBean(DLXConsumer.class)
    DLXConsumer defaultDlxConsumer(){
        return new DefaultDlxConsumer();
    }

    @Autowired
    DLXConsumer consumer;

    @PostConstruct
    void init(){
        connection = connectionFactory.createConnection();
        IntStream.of(channelCount).forEach(value -> {
            try {
                channels.add(channel = connection.createChannel(false));
                channel.queueDeclare(queueN,true,true,false,null);
                channel.exchangeDeclare(exchangeN, BuiltinExchangeType.TOPIC,true);
                channel.queueBind(queueN,exchangeN,"#");

                channel.basicConsume(queueN,new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        AisMessage dlxMessage = null;
                        try {
                            dlxMessage = bytesToAisMessage(body);
                        } catch (AisException e) {
                            e.printStackTrace();
                        }
                        consumer.process(dlxMessage);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
