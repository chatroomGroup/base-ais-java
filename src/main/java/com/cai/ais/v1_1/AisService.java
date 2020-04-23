package com.cai.ais.v1_1;

import org.springframework.stereotype.Service;

@Service
public abstract class AisService<T extends AisMessage> {
    public abstract void process(T msg);

}
