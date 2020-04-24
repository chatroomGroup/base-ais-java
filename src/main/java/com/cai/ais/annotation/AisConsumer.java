package com.cai.ais.annotation;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface AisConsumer {

    String type() default ExchangeTypes.DIRECT;

    String exchangeName() default "";

    String routeKey() default "";

    String queue();

}
