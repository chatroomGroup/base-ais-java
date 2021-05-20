package com.cai.ais.config;

import com.cai.ais.core.AisData;
import com.cai.ais.core.exception.AisException;
import com.rabbitmq.client.BuiltinExchangeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class AloneQueueRegistrar {

    QueueChannelMap queueChannelMap;

    public AloneQueueRegistrar(QueueChannelMap queueChannelMap) {
        this.queueChannelMap = queueChannelMap;
    }

    public void register(String queue, int channelCount) throws AisException {
        queueChannelMap.createChannel(queue ,channelCount);

    }

    public QueueChannelMap getQueueChannelMap() {
        return queueChannelMap;
    }
}
