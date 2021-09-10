package com.cai.ais.core.client;

import com.cai.ais.config.AisProperties;
import com.cai.ais.config.AisService;
import com.cai.ais.config.AloneQueueRegistrar;
import com.cai.ais.config.QueueChannelMap;
import com.cai.ais.core.AisConfiguration;
import com.cai.ais.core.exception.AisException;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.MessageFormat;
import java.util.Map;
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

    static String CONSUMER_METHOD_NAME = "process";
    @Bean
    public SimpleMessageListenerContainer listenerContainer(MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(aisProperties.getConcurrency());
        container.setMaxConcurrentConsumers(aisProperties.getMaxConcurrency());
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setReceiveTimeout(10000);
        container.setPrefetchCount(1);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                AisService ais = (AisService) queueToObject.get(message.getMessageProperties().getConsumerQueue());
                log.info(MessageFormat.format("exchange: [ {0} ,routeKey: {1} ] is executing",message.getMessageProperties().getReceivedExchange(),message.getMessageProperties().getReceivedRoutingKey()));
                listenerAdapter.setDelegate(ais);
                listenerAdapter.setDefaultListenerMethod(CONSUMER_METHOD_NAME);
                listenerAdapter.onMessage(message, channel);
            }
        });
        return container;
    }

    @Bean
//    @ConditionalOnProperty(name = "ais.mq.aloneQueue")
    AloneQueueRegistrar aloneQueueRegistrar(@Autowired AmqpAdmin amqpAdmin){
        Map<String,Long> aq = aisProperties.getAloneQueue();
        Connection aloneConnect = connectionFactory.createConnection();
        AloneQueueRegistrar aqReg = new AloneQueueRegistrar(new QueueChannelMap(aloneConnect,this));
        aq.forEach((k,v)->{
            try {
                aqReg.register(k, Math.toIntExact(v));
            } catch (AisException e) {
                e.printStackTrace();
            }
        });
        aqReg.getQueueChannelMap().getConsumerProcessProxyMap().forEach((k,v)->{
            v.createChannel();
        });
        return aqReg;
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
    public Object bytesToObject(byte[] bytes) throws IOException {
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

    public ConcurrentMap<String, Object> getQueueToObject() {
        return queueToObject;
    }
}
