package com.cai.ais.v1_1;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.Serializable;
import java.util.Map;

public class AisMessage<T> extends Message implements Serializable {

    public AisMessage(T body, MessageProperties messageProperties) {
        super((byte[]) body, messageProperties);
    }


}
