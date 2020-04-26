package com.cai.ais.consumer;

import com.cai.ais.AisMessage;
import com.cai.ais.AisService;
import com.cai.ais.annotation.TopicConsumerListener;

@TopicConsumerListener(routeKey = "erp.log.#", exchangeName = "com.customer.topic")
public class TopicPrint2Consumer extends AisService<AisMessage> {

    @Override
    public void process(AisMessage msg) {
        System.out.println("erp" + msg.getBody());
    }
}
