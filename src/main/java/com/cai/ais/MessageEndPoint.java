package com.cai.ais;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@interface MessageEndPoint {
    String errorRouteKey = "Error";

    String value() default "";

    String queue();

    MessageExchangeType exchange() default MessageExchangeType.DIRECT;

    String routeKey() default errorRouteKey;
}