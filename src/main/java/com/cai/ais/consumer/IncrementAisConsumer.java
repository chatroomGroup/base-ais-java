package com.cai.ais.consumer;

import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisService;
import com.cai.ais.annotation.ConsumerListener;

import java.util.concurrent.atomic.AtomicInteger;

@ConsumerListener(queue = "test.001")
public class IncrementAisConsumer extends AisService<AisMessage<String>> {
    static AtomicInteger v = new AtomicInteger(1);
    @Override
    public Object process(AisMessage<String> msg) {
        System.out.println(msg.getBody()+v.incrementAndGet());
        return String.valueOf(msg.getBody()+v.incrementAndGet());
    }
}
