package com.cai.ais;

import java.io.Serializable;
import java.util.Map;

public class AisMessage<T> implements Serializable {
    private Long id;
    private String queue;
    private T body;
    private String msgFrom;
    private String msgTo;
    private Map<String,Object> params;

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    String getQueue() {
        return queue;
    }

    void setQueue(String queue) {
        this.queue = queue;
    }

    T getBody() {
        return body;
    }

    void setBody(T body) {
        this.body = body;
    }

    String getMsgFrom() {
        return msgFrom;
    }

    void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    String getMsgTo() {
        return msgTo;
    }

    void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    Map<String, Object> getParams() {
        return params;
    }

    void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
