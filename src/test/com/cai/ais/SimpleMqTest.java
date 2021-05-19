package com.cai.ais;

import com.cai.ais.AisApplication;
import com.cai.ais.config.AisMessage;
import com.cai.ais.core.send.AisSend;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.ChannelN;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@SpringBootTest(classes = {AisApplication.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleMqTest {
    Logger log = LoggerFactory.getLogger(SimpleMqTest.class);
    @Autowired
    AisSend aisSend;

    @Test
    public void fanoutTest(){
        AisMessage message = new AisMessage<String>();
        message.setBody("123");
//        aisSend.send(message,"com.reply.fanout");
        Object rs = aisSend.sendAndReceive(message,"com.generate.fanout");
        System.out.println("re: "+rs);
    }

    @Test
    public void topicTest(){
        AisMessage message = new AisMessage<String>();
        message.setBody("topic123 ");
        aisSend.send(message,"com.customer.topic","erp.log.error");
    }

    Map<String, Integer> queueInfo = new HashMap<String, Integer>(){{
        put("com.cai.print1",10);
    }};

    ConnectionFactory connectionFactory;

    QueueChannelMap qcMap;

    @Before
    public void before() throws IOException, TimeoutException {
        createConnectionFactory();
        loadAloneChannel(connectionFactory);
    }


    void createConnectionFactory(){
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/cr-3");
    }

    void loadAloneChannel(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
        qcMap = new QueueChannelMap(connectionFactory.newConnection());
        queueInfo.forEach((k,v)->{
            qcMap.createChannel(k, v);
        });
    }

    static class QueueChannelMap {

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
                    channels.add(channel = target.createChannel(1));
                    channel.exchangeDeclare(queue, BuiltinExchangeType.DIRECT);
                    channel.queueDeclare(queue, true, true,false, null);
                    channel.exchangeBind(queue, queue, queue);
                }
                aloneChannels.put(queue, channels);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Channel findChannel(String queue){
//            return aloneChannels.getOrDefault(queue, null);
            return null;
        }
    }

    @Test
    public void aloneTest() throws IOException {
        String message = "test-test";
        qcMap.findChannel("com.cai.print1").basicPublish("com.cai.print1","com.cai.print1", null, message.getBytes(StandardCharsets.UTF_8));
    }
}
