package com.cai.ais.core.client;

import com.cai.ais.AisMessage;
import com.cai.ais.AisProperties;
import com.cai.ais.AisService;
import com.cai.ais.core.AisConfiguration;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Import(AisConfiguration.class)
@Configuration
public class ConsumerConfiguration {

    private ConcurrentMap<String,Object> queueToObject = new ConcurrentHashMap<>();

    Logger log = LoggerFactory.getLogger(ConsumerConfiguration.class);

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    MessageConverter converter;

    @Autowired
    AisProperties aisProperties;

    @Bean
    public SimpleMessageListenerContainer listenerContainer(MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(aisProperties.getConcurrency());
        container.setMaxConcurrentConsumers(aisProperties.getMaxConcurrency());
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setReceiveTimeout(10000);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                AisService ais = (AisService) queueToObject.get(message.getMessageProperties().getConsumerQueue());
                log.info(MessageFormat.format("exchange: [ {0} ,routeKey: {1} ] is executing",message.getMessageProperties().getReceivedExchange(),message.getMessageProperties().getReceivedRoutingKey()));
                listenerAdapter.setDelegate(ais);
                listenerAdapter.setDefaultListenerMethod("process");
                listenerAdapter.onMessage(message, channel);
            }
        });
        return container;
    }

    @Bean
    Queue replyQueue(){
        return QueueBuilder.durable("amq.rabbitmq.reply-to").build();
    }

    @Bean
    @Scope("prototype")
    public MessageListenerAdapter listenerAdapterContainer(){
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        return adapter;
    }


    @Deprecated
    private Object bytesToObject(byte[] bytes) throws IOException {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public void addQueueToObjectItem(String queueName, Object o) {
        this.queueToObject.putIfAbsent(queueName,o);
    }
}
