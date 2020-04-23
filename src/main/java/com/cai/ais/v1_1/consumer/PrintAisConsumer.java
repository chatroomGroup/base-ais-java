package com.cai.ais.v1_1.consumer;

import com.cai.ais.v1_1.AisMessage;
import com.cai.ais.v1_1.AisService;
import com.cai.ais.v1_1.annotation.AisConsumer;
import com.cai.ais.v1_1.annotation.ConsumerListener;
import com.rabbitmq.http.client.domain.ExchangeType;
import org.springframework.amqp.core.ExchangeTypes;

@ConsumerListener()
public class PrintAisConsumer extends AisService<AisMessage<String>> {

    @Override
    public void process(AisMessage msg) {
        System.out.println(msg);
    }
}
