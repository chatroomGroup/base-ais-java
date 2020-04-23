package com.cai.ais.v1_1.consumer;

import com.cai.ais.v1_1.AisMessage;
import com.cai.ais.v1_1.AisService;
import com.cai.ais.v1_1.annotation.ConsumerListener;

import java.util.concurrent.atomic.AtomicInteger;

@ConsumerListener()
public class IncrementAisConsumer extends AisService<AisMessage<String>> {
    static AtomicInteger v = new AtomicInteger(1);
    @Override
    public void process(AisMessage msg) {
        System.out.println(new String(msg.getBody())+v.incrementAndGet());
    }
}
