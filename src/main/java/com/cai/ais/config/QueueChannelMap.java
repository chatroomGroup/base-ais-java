package com.cai.ais.config;

import com.rabbitmq.client.*;
import org.springframework.amqp.rabbit.connection.Connection;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class QueueChannelMap {

    static Random random = new Random();

    Map<String, List<Channel>> aloneChannels;

    Connection target;

    public QueueChannelMap(Connection connection){
        aloneChannels = new HashMap<>();
        this.target = connection;
    }

    public void createChannel(String queue, int count){
        try {
            Channel channel;
            List<Channel> channels = new ArrayList<>();
            for (int i = 0 ; i < count ; i++){
                channels.add(channel = target.createChannel(true));
                channel.exchangeDeclare(queue, BuiltinExchangeType.DIRECT,false);
                channel.queueDeclare(queue, false, true,false, null);
                channel.exchangeBind(queue, queue, queue);
                channel.queueBind(queue,queue,queue);
                Consumer consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        System.out.println(MessageFormat.format("consumerTag: {0}", consumerTag));
                        System.out.println(new String(body));
                    }

                };
                channel.basicQos(1);
                channel.basicConsume(queue, consumer);

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
}