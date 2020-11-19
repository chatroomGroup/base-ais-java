package com.cai.ais.test;

import com.cai.ais.AisApplication;
import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisProperties;
import com.cai.ais.config.AisService;
import com.cai.ais.core.AisConfiguration;
import com.cai.ais.core.client.ConsumerConfiguration;
import com.cai.ais.core.send.AisSend;
import com.rabbitmq.client.Channel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.MessageFormat;

@Import({AisConfiguration.class})
@SpringBootTest(classes = {AisApplication.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleMqTest {
    Logger log = LoggerFactory.getLogger(SimpleMqTest.class);
    @Autowired
    AisSend aisSend;

    @Autowired
    AisProperties aisProperties;

    @Autowired
    ConsumerConfiguration consumerConfiguration;

    @Test
    public void fanoutTest(){
        AisMessage message = new AisMessage<String>();
        message.setBody("123");
//        aisSend.send(message,"com.reply.fanout");
        Object rs = aisSend.sendAndReceive(message,"com.generate.fanout");
        System.out.println("re: "+rs);
    }
    static String CONSUMER_METHOD_NAME = "process";

    SimpleMessageListenerContainer container;

    void messageListenerContainer(){
        ConnectionFactory connectionFactory = connectionFactory();
        MessageListenerAdapter listenerAdapter = listenerAdapterContainer();
        container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(10);
        container.setMaxConcurrentConsumers(10);
        container.setPrefetchCount(1);
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setReceiveTimeout(10000);
        container.setQueueNames("com.cai.print1","com.cai.print2");
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                AisService ais = (AisService) consumerConfiguration.getQueueToObject().get(message.getMessageProperties().getConsumerQueue());
                log.info(MessageFormat.format("exchange: [ {0} ,routeKey: {1} ] is executing",message.getMessageProperties().getReceivedExchange(),message.getMessageProperties().getReceivedRoutingKey()));
                listenerAdapter.setDelegate(ais);
                listenerAdapter.setDefaultListenerMethod(CONSUMER_METHOD_NAME);
                listenerAdapter.onMessage(message, channel);
            }
        });
    }

    public MessageListenerAdapter listenerAdapterContainer(){
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        return adapter;
    }


    ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(aisProperties.getUsername());
        connectionFactory.setPassword(aisProperties.getPassword());
        connectionFactory.setVirtualHost(aisProperties.getVirtualHost());
        connectionFactory.setHost(aisProperties.getHost());
        connectionFactory.setPort(aisProperties.getPort());
        return connectionFactory;
    }
    @Test
    public void topicTest(){
//        messageListenerContainer();

//        while (true){

        AisMessage message = new AisMessage<String>();
        message.setBody("topic123");
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(container.getConnectionFactory());
//        rabbitTemplate.convertAndSend("com.customer.topic", "erp.log.error", message);
        aisSend.send(message,"com.customer.topic","erp.log.error");
//        }
    }

    @Test
    public void aloneQueueConcurrentConsumers(){

    }

}
