package com.cai.ais.v1_1.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ConsumerListener {
    String queue() default "";

    String exchangeName() default "com.generate.direct";
}