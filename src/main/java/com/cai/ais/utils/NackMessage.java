package com.cai.ais.utils;

import com.cai.ais.config.AisMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NackMessage {

    AisMessage body;

    String queue;

    String exchange;

    String created = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    public NackMessage(AisMessage body, String queue, String exchange) {
        this.body = body;
        this.queue = queue;
        this.exchange = exchange;
    }
}
