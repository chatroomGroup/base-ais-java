package com.cai.ais.config;

import org.springframework.stereotype.Service;

@Service
public abstract class AisService<T extends AisMessage> {
    public abstract Object process(T msg);

}