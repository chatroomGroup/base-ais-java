package com.cai.ais.consumer;

import com.cai.ais.AisMessage;
import com.cai.ais.AisService;
import com.cai.ais.annotation.ConsumerListener;

import java.util.concurrent.atomic.AtomicInteger;

@ConsumerListener()
public class IncrementAisConsumer extends AisService<AisMessage<String>> {
    static AtomicInteger v = new AtomicInteger(1);
    @Override
    public void process(AisMessage<String> msg) {
        System.out.println(msg.getBody()+v.incrementAndGet());
    }
}
