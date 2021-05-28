package com.cai.ais.config;

import com.rabbitmq.client.BuiltinExchangeType;

public interface ChannelCreator<T> {

    ConsumerProcessProxy createProxy(T proxy,
                                     String queue,
                                     String exchangeName,
                                     String routeKey,
                                     BuiltinExchangeType type, int channelNum);

    ConsumerProcessProxy getProxy();
}
