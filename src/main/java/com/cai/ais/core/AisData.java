package com.cai.ais.core;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;

import java.util.*;

public class AisData {

    private static List<String> exchangeNames = new ArrayList<>();

    private static Map<String,Exchange> exchanges = new LinkedHashMap<>();

    public static Exchange addAndReturnExchange(String exchangeName, AmqpAdmin amqpAdmin){
        if (exchangeNames.contains(exchangeName))
            return exchanges.get(exchangeName);
        Exchange exchange = ExchangeBuilder.fanoutExchange(exchangeName).build();
        amqpAdmin.declareExchange(exchange);
        exchangeNames.add(exchangeName);
        exchanges.put(exchangeName,exchange);
        return exchange;
    }
}
