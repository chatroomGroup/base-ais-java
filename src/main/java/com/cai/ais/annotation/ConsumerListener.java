package com.cai.ais.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ConsumerListener {

    String queue() default "";

    String exchangeName() default "com.generate.fanout";
}
