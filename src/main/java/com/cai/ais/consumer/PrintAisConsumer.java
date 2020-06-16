package com.cai.ais.consumer;

import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisService;
import com.cai.ais.annotation.ConsumerListener;

@ConsumerListener(queue = "test.002")
public class PrintAisConsumer extends AisService<AisMessage<String>> {

    @Override
    public Object process(AisMessage msg) {
        System.out.println(msg.getBody());
        return msg.getBody();
    }
}
