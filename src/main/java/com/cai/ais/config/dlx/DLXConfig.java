package com.cai.ais.config.dlx;

import com.cai.ais.config.ConsumerProcessProxy;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 死信队列的配置
 */
@ConditionalOnBean(value = DLXConsumer.class)
@Configuration
public class DLXConfig {

    private static final String queueN = "dlx.queue";

    private static final String exchangeN = "dlx.exchange";

    private final Integer channelCount = 10;

    private Connection connection;

    private Channel channel;

    private List<Channel> channels = new ArrayList<>();

    private static Map<String, Object> arguments = new LinkedHashMap<>();

    static {
        arguments.put("x-dead-letter-exchange", exchangeN);
    }

    @Autowired
    ConnectionFactory connectionFactory;

    @PostConstruct
    void init(){
        connection = connectionFactory.createConnection();
        IntStream.of(channelCount).forEach(value -> {
            try {
                channels.add(channel = connection.createChannel(false));
                channel.queueDeclare(queueN,true,true,false,arguments);
                channel.exchangeDeclare(exchangeN, BuiltinExchangeType.TOPIC,true);
                channel.queueBind(queueN,exchangeN,"#");

                channel.basicConsume(queueN,new ConsumerProcessProxy.CustomerConsumer(){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        super.handleDelivery(consumerTag, envelope, properties, body);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
