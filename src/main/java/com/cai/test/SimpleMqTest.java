package com.cai.test;

import com.cai.ChatroomBusinessApplication;
import com.cai.ais.AisMessage;
import com.cai.ais.core.send.AisSend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = {ChatroomBusinessApplication.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleMqTest {

    @Autowired
    AisSend aisSend;

    @Test
    public void fanoutTest(){
        AisMessage message = new AisMessage<String>();
        message.setBody("123");
        aisSend.send(message,"com.generate.fanout");
    }

    @Test
    public void topicTest(){
        AisMessage message = new AisMessage<String>();
        message.setBody("topic123 ");
        aisSend.send(message,"com.generate.topic","erp.log.error");
    }

}
