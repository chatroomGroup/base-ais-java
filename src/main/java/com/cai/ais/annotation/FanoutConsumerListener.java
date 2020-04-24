package com.cai.ais.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface FanoutConsumerListener {

    String exchangeName() default "com.generate.fanout";

}
