package com.cai.ais.consumer;

import com.cai.ais.annotation.ConsumerListener;
import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisService;

import java.util.UUID;

@ConsumerListener(queue = "queue.receive.print", exchangeName = "exchange.receive.print")
public class ReceivePrintConsumer extends AisService<AisMessage> {

    @Override
    public Object process(AisMessage msg) {
        System.out.println(msg.getBody());
        return UUID.randomUUID().toString();
    }
}
