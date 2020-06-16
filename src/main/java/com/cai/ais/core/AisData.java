package com.cai.ais.core;

import com.cai.ais.config.MessageExchangeType;
import com.cai.ais.core.exception.AisException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;

import java.util.*;

public class AisData {

    private static List<String> exchangeNames = new ArrayList<>();

    private static Map<String,Exchange> exchanges = new LinkedHashMap<>();

    public static Exchange addAndReturnExchange(String exchangeName, AmqpAdmin amqpAdmin, MessageExchangeType type) throws AisException {
        if (exchangeNames.contains(exchangeName))
            return exchanges.get(exchangeName);
        Exchange exchange = getExchangeByType(exchangeName,type);
        amqpAdmin.declareExchange(exchange);
        exchangeNames.add(exchangeName);
        exchanges.put(exchangeName,exchange);
        return exchange;
    }

    private static Exchange getExchangeByType(String exchangeName, MessageExchangeType type) throws AisException {
        switch (type){
            case TOPIC:
                return ExchangeBuilder.topicExchange(exchangeName).build();
            case FANOUT:
                return ExchangeBuilder.fanoutExchange(exchangeName).build();
            case DIRECT:
                throw new AisException("Direct is Unsupported type");
            default:
                throw new AisException("Unsupported type");
        }
    }
}
