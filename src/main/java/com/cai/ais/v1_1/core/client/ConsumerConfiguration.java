package com.cai.ais.v1_1.core.client;

import com.cai.ais.AisMessage;
import com.cai.ais.AisService;
import com.cai.ais.v1_1.core.AisConfiguration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Import(AisConfiguration.class)
@Configuration
public class ConsumerConfiguration {

    private ConcurrentMap<String,Object> queueToObject = new ConcurrentHashMap<>();

    private static Object LOCK = new Object();


    @Autowired
    ConnectionFactory connectionFactory;

    @Bean
    public SimpleMessageListenerContainer listenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageListener(message -> {
            if(queueToObject.get(message.getMessageProperties().getConsumerQueue())!=null){
                AisService ais = (AisService) queueToObject.get(message.getMessageProperties().getConsumerQueue());
                System.out.println(message.toString());
                AisMessage message1 = (AisMessage)bytesToObject(message.getBody());
                ais.process(message1);
            }
        });
        return container;
    }

    private Object bytesToObject(byte[] bytes){
        Object obj = null;
        try {
            synchronized (LOCK) {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis);
                obj = ois.readObject();
                ois.close();
                bis.close();
            }
        } catch (ClassNotFoundException ex) {
			ex.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
        }
        return obj;
    }

    public void addQueueToObjectItem(String queueName, Object o) {
        this.queueToObject.putIfAbsent(queueName,o);
    }
}
