package com.cai.ais.consumer;

import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisService;
import com.cai.ais.annotation.TopicConsumerListener;

@TopicConsumerListener(queue = "com.cai.print1", routeKey = "*.log.*",exchangeName = "com.customer.topic")
public class TopicPrint1Consumer extends AisService<AisMessage> {

    @Override
    public Object process(AisMessage msg) {
        System.out.println(msg.getBody());
        return null;
    }
}
