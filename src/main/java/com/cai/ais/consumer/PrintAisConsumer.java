package com.cai.ais.consumer;

import com.cai.ais.AisMessage;
import com.cai.ais.AisService;
import com.cai.ais.annotation.ConsumerListener;

@ConsumerListener
public class PrintAisConsumer extends AisService<AisMessage<String>> {

    @Override
    public void process(AisMessage msg) {
        System.out.println(msg.getBody());
    }
}
