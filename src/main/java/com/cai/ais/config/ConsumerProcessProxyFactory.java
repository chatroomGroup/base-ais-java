package com.cai.ais.config;

import com.rabbitmq.client.BuiltinExchangeType;
import org.springframework.amqp.rabbit.connection.Connection;

public class ConsumerProcessProxyFactory {

    public static ConsumerProcessProxy create(AisService proxy, Connection connection, String queue, String exchangeName, String routeKey, BuiltinExchangeType type, int number){
        return new ConsumerProcessProxy(proxy, connection, queue, exchangeName, routeKey, type, number);
    }

    public static ConsumerProcessProxy create(AisService proxy, Connection connection , String queue, String exchangeName, String routeKey, BuiltinExchangeType type){
        return new ConsumerProcessProxy(proxy,  connection, queue, exchangeName, routeKey, type, 1);
    }
}
