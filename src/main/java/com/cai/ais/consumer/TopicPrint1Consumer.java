package com.cai.ais.consumer;

import com.cai.ais.AisMessage;
import com.cai.ais.AisService;
import com.cai.ais.annotation.TopicConsumerListener;

@TopicConsumerListener(routeKey = "*.log.*",exchangeName = "com.customer.topic")
public class TopicPrint1Consumer extends AisService<AisMessage> {

    @Override
    public Object process(AisMessage msg) {
        System.out.println(msg.getBody());
        return null;
    }
}
