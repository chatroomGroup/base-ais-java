package com.cai.ais;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cai.ais.MessageExchangeType.*;

@Configuration
public class MessageConfig {

    @Autowired
    ApplicationContext context;

    @Autowired
    AisProperties aisProperties;

    @Bean(name = "rabbitAdmin")
    RabbitAdmin rabbitAdmin(@Qualifier("connectionFactory") ConnectionFactory myConnectionFactory){
        return new RabbitAdmin(myConnectionFactory);
    }

    @Bean(name = "myContainer")
    SimpleMessageListenerContainer myContainer(@Qualifier("connectionFactory") ConnectionFactory myConnectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(myConnectionFactory);
//        //监听容器配置队列，队列需要加入ConnectionFactory中
//        Queue[] queues = consumerConfig.queues.toArray(new Queue[consumerConfig.queues.size()]);
//        if (queues.length != 0) {
//            container.setQueues(queues);
//        } else {
//            container.setQueues(new Queue("test.test"));
//        }
//
//        String[] queuesName = consumerConfig.queuesName.toArray(new String[consumerConfig.queuesName.size()]);
//        if (queuesName.length != 0) {
//            container.setQueueNames(queuesName);
//        } else {
//            container.setQueueNames("test.test");
//        }
        container.setConnectionFactory(myConnectionFactory);
        container.setConcurrentConsumers(aisProperties.getConcurrency());
        container.setMaxConcurrentConsumers(aisProperties.getMaxConcurrency());
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }

    @Bean(name = "messageListenerAdapter")
    MessageListenerAdapter messageListenerAdapter(@Qualifier("myContainer") SimpleMessageListenerContainer container){
        MessageListenerAdapter adapter = new MessageListenerAdapter();
//        Map<String,String> queryOrTagToMethodName = new HashMap(consumerConfig.quesesMethods);
//        //适配器中设置队列与监听方法的对应
//        if (queryOrTagToMethodName.size() > 0) {
//            adapter.setQueueOrTagToMethodName(queryOrTagToMethodName);
//        }
//        container.setMessageListener(adapter);
//        container.setQueueNames()
        return adapter;
    }

}
