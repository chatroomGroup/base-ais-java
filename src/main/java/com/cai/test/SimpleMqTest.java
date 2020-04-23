package com.cai.test;

import com.cai.ChatroomBusinessApplication;
import com.cai.ais.v1_1.core.send.AisSend;
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
    public void mqTest(){
        aisSend.send("hhh","com.generate.fanout");
    }

}
