package com.cai.ais.consumer;

import com.cai.ais.annotation.ConsumerListener;
import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisService;

import java.util.concurrent.atomic.AtomicInteger;

@ConsumerListener(queue = "com.cai.alone.queue", exchangeName = "com.cai.alone.queue")
public class AloneQueueConsumer extends AisService<AisMessage<String>> {
    static AtomicInteger v = new AtomicInteger(1);
    @Override
    public Object process(AisMessage<String> msg) {
        System.out.println(msg.getBody());
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
//        return String.valueOf(msg.getBody()+v.incrementAndGet());
    }
}
