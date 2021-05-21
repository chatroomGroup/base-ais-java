package com.cai.ais.config;

import com.cai.ais.core.AisData;
import com.cai.ais.core.client.ConsumerConfiguration;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class QueueChannelMap{

    Logger log = LoggerFactory.getLogger(QueueChannelMap.class);

    WebApplicationContext ctx;

    static Random random = new Random();

    Map<String, List<Channel>> aloneChannels;

    Connection target;

    ConsumerConfiguration consumerConfiguration;


    public QueueChannelMap(Connection connection, ConsumerConfiguration consumerConfiguration){
        aloneChannels = new HashMap<>();
        this.target = connection;
        this.consumerConfiguration = consumerConfiguration;
    }

    public void createChannel(String queue, int count){
        try {
            Channel channel;
            List<Channel> channels = new ArrayList<>();
            for (int i = 0 ; i < count ; i++){
                channels.add(channel = target.createChannel(false));
                channel.exchangeDeclare(queue, BuiltinExchangeType.DIRECT,false);
                channel.queueDeclare(queue, true, false,false, null);
                channel.exchangeBind(queue, queue, queue);
                channel.queueBind(queue,queue,queue);
                Consumer consumer = new CustomerConsumer(queue, channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        AisService as = (AisService) consumerConfiguration.getQueueToObject().get(queue);
                        Object o = consumerConfiguration.bytesToObject(body);
                        log.info(MessageFormat.format("consumerTag: {0}", consumerTag));
                        as.process((AisMessage) o);
                        log.info("消息处理成功");
                    }

                };
                channel.basicQos(64);
                channel.basicConsume(queue,true, consumer);

            }
            aloneChannels.put(queue, channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel findChannel(String queue){
        return getChannel(aloneChannels.get(queue));
    }

    static Channel getChannel(List<Channel> channels){
        return channels.get(random.nextInt(channels.size()));
    }


    class CustomerConsumer extends DefaultConsumer{

        String queue;
        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public CustomerConsumer(String queue, Channel channel) {
            super(channel);
            this.queue = queue;
        }
    }
}