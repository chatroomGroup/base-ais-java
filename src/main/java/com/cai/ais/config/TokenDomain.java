package com.cai.ais.config;

import java.io.Serializable;

public class TokenDomain implements Serializable {
    String token;
    String client;

    public TokenDomain(String token, String client) {
        this.token = token;
        this.client = client;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

}
