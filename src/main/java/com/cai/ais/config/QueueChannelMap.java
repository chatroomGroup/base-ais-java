package com.cai.ais.config;

import com.cai.ais.core.AisData;
import com.cai.ais.core.client.ConsumerConfiguration;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class QueueChannelMap{

    Logger log = LoggerFactory.getLogger(QueueChannelMap.class);

    Connection target;

    ConsumerConfiguration consumerConfiguration;

    Map<String, ConsumerProcessProxy> consumerProcessProxyMap;

    public QueueChannelMap(Connection connection, ConsumerConfiguration consumerConfiguration){
        this.target = connection;
        this.consumerConfiguration = consumerConfiguration;
        consumerProcessProxyMap = new HashMap<>();
    }

    public void createChannel(String queue, int count){
        AisService as = (AisService) consumerConfiguration.getQueueToObject().get(queue);
        consumerProcessProxyMap.put(queue,ConsumerProcessProxyFactory.create(as,target,queue,queue,queue,BuiltinExchangeType.DIRECT,count));
    }

    public Map<String, ConsumerProcessProxy> getConsumerProcessProxyMap() {
        return consumerProcessProxyMap;
    }
}