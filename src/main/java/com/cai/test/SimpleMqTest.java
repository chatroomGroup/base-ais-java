package com.cai.test;

import com.cai.Application;
import com.cai.ais.AisMessage;
import com.cai.ais.core.send.AisSend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFutureCallback;

@SpringBootTest(classes = {Application.class})
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

}
