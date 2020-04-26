package com.cai.ais.core.client;

import com.cai.ais.AisMessage;
import com.cai.ais.AisService;
import com.cai.ais.core.AisConfiguration;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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

    @Bean
    public SimpleMessageListenerContainer listenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageListener(message -> {
            if(queueToObject.get(message.getMessageProperties().getConsumerQueue())!=null){
                try {
                    AisService ais = (AisService) queueToObject.get(message.getMessageProperties().getConsumerQueue());
                    log.info(MessageFormat.format("exchange: [ {0} ,routeKey: {1} ] is executing",message.getMessageProperties().getReceivedExchange(),message.getMessageProperties().getReceivedRoutingKey()));
                    AisMessage message1 = (AisMessage) converter.fromMessage(message);
                    ais.process(message1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return container;
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
