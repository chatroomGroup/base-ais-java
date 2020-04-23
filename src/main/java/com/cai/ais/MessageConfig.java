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
    MessageConsumerConfig consumerConfig;

    @Autowired
    ApplicationContext context;

    @Autowired
    AisProperties aisProperties;

//    @Bean
//    ConnectionFactory connectionFactory(){
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setUsername(aisProperties.getUsername());
//        connectionFactory.setPassword(aisProperties.getPassword());
//        connectionFactory.setVirtualHost(aisProperties.getVirtualHost());
//        connectionFactory.setHost(aisProperties.getHost());
//        connectionFactory.setPort(aisProperties.getPort());
//        return connectionFactory;
//    }

//    @Bean
//    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
//        return new RabbitTemplate(connectionFactory);
//    }
    @Bean(name = "rabbitAdmin")
    RabbitAdmin rabbitAdmin(@Qualifier("connectionFactory") ConnectionFactory myConnectionFactory){
        try{
            RabbitAdmin rabbitAdmin = new RabbitAdmin(myConnectionFactory);
            rabbitAdmin.declareQueue(new Queue("test.test"));
//        rabbitAdmin.setAutoStartup(true)
            consumerConfig.queues.forEach(it -> {
                rabbitAdmin.declareQueue(it);
                MessageExchangeType type = consumerConfig.queuesType.get(it.getName());
                switch (type){
                    case TOPIC:
                        String routeKey = consumerConfig.queuesRoutekeys.get(it.getName());
                        if (routeKey == MessageEndPoint.errorRouteKey){
                            try {
                                throw new MessageException(routeKey,it.getName() + "routeKey is not null!!!");
                            } catch (MessageException e) {
                                e.printStackTrace();
                            }
                        }
                        rabbitAdmin.declareExchange(new TopicExchange("test.topic",false,false));
                        rabbitAdmin.declareBinding(BindingBuilder.bind(it)
                                .to(new TopicExchange("test.topic",false,false))
                                .with(routeKey)
                        );
                        break;
                    case DIRECT:
                        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
                        rabbitAdmin.declareBinding(BindingBuilder
                                .bind(it)
                                .to(new DirectExchange("test.direct", false, false))
                                .withQueueName());
                        break;
                    case FANOUT:
                        break;
                    default:
                        try {
                            throw new MessageException("未实现的交换机类型");
                        } catch (MessageException e) {
                            e.printStackTrace();
                        }
                }
            });
            return rabbitAdmin;
        }catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }

    @Bean(name = "myContainer")
    SimpleMessageListenerContainer myContainer(@Qualifier("connectionFactory") ConnectionFactory myConnectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(myConnectionFactory);
        //监听容器配置队列，队列需要加入ConnectionFactory中
        Queue[] queues = consumerConfig.queues.toArray(new Queue[consumerConfig.queues.size()]);
        if (queues.length != 0) {
            container.setQueues(queues);
        } else {
            container.setQueues(new Queue("test.test"));
        }

        String[] queuesName = consumerConfig.queuesName.toArray(new String[consumerConfig.queuesName.size()]);
        if (queuesName.length != 0) {
            container.setQueueNames(queuesName);
        } else {
            container.setQueueNames("test.test");
        }

        container.setConcurrentConsumers(aisProperties.getConcurrency());
        container.setMaxConcurrentConsumers(aisProperties.getMaxConcurrency());
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }

    @Bean(name = "messageListenerAdapter")
    MessageListenerAdapter messageListenerAdapter(@Qualifier("myContainer") SimpleMessageListenerContainer container){
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        Map<String,String> queryOrTagToMethodName = new HashMap(consumerConfig.quesesMethods);
        //适配器中设置队列与监听方法的对应
        if (queryOrTagToMethodName.size() > 0) {
            adapter.setQueueOrTagToMethodName(queryOrTagToMethodName);
        }
        container.setMessageListener(adapter);
//        container.setQueueNames()
        return adapter;
    }

    /*
    1.
    设置监听器的目标对象
    另一种方式是实现container.setMessageListener方法，优先级高于该方法，见Fanout与Topic
     */
    void setDelegate(Object o){
        Assert.notNull(context.getBean("messageListenerAdapter"),"MessageListenerAdapter is not bean");
        ((MessageListenerAdapter)context.getBean("messageListenerAdapter")).setDelegate(o);
    }


    /*
    设置消费的具体实现
     */
    void setListenerMethod() {
        SimpleMessageListenerContainer container = (SimpleMessageListenerContainer)context.getBean("myContainer");
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                AisService aisService = (AisService)context.getBean((String) consumerConfig.queses.get(message.getMessageProperties().getConsumerQueue()));
                AisMessage body = (AisMessage) toT(message.getBody());
                aisService.process(body);
            }
        });
    }
    /*
    字节码转换成对象的方法
     */

    public <T> T toT(byte[] bytes) throws IOException, ClassNotFoundException {
        //转换成实体类
        ByteArrayInputStream bais = null;
        bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        T body = (T)ois.readObject();
        return body;
    }

    /*
    设置Fanout类型交换机的绑定队列 一对多
     */
    void declareQueuesToExchange(String exchange,List<String> queues){
        RabbitAdmin rabbitAdmin = (RabbitAdmin) context.getBean("rabbitAdmin");
        Assert.notNull(rabbitAdmin);
        for (String queue : queues){
            rabbitAdmin.declareBinding(BindingBuilder
                    .bind(new Queue(queue,false))
                    .to(new FanoutExchange(exchange,false,false))
            );
        }
    }

    class MessageException extends Exception{
        String msg;
        Object errorBody;
        MessageException(Object errorBody,String msg){
            super(msg);
            this.errorBody = errorBody;
            this.msg = msg;
        }

        MessageException(String msg){
            super(msg);
            this.msg = msg;
        }
    }


}
