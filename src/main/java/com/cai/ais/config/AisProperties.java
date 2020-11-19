package com.cai.ais.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ais.mq")
public class AisProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;
    private int concurrency;
    private int maxConcurrency;
    private boolean listen;
    private Map<String, Long> queueConcurrency;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public boolean isListen() {
        return listen;
    }

    public void setListen(boolean listen) {
        this.listen = listen;
    }

    public Map<String, Long> getQueueConcurrency() {
        return queueConcurrency;
    }

    public void setQueueConcurrency(Map<String, Long> queueConcurrency) {
        this.queueConcurrency = queueConcurrency;
    }
}
