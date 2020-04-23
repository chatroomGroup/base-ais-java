package com.cai.ais.v1_1.core.client;

import com.cai.ais.v1_1.AisService;
import com.cai.ais.v1_1.annotation.ConsumerListener;
import com.cai.ais.v1_1.annotation.FanoutConsumerListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.Map;

@Component
public class ConsumerListenerBeanProcessor implements BeanPostProcessor {

    @Autowired
    ConsumerConfiguration consumerConfig;

    @Autowired
    SimpleMessageListenerContainer listenerContainer;

    @Autowired
    RabbitAdmin amqpAdmin;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(ConsumerListener.class)
                || bean.getClass().isAnnotationPresent(FanoutConsumerListener.class)){
            checkAndThrowCoexist(bean.getClass().isAnnotationPresent(FanoutConsumerListener.class)
                    && bean.getClass().isAnnotationPresent(ConsumerListener.class)
            );
            if (bean instanceof AisService){
                if(bean.getClass().isAnnotationPresent(ConsumerListener.class)){
                    ConsumerListener listener = bean.getClass().getAnnotation(ConsumerListener.class);
                    Map values = AnnotationUtils.getAnnotationAttributes(ConsumerListener.class,listener);
                    declareAndBind((String) values.get("queue"), (String) values.get("exchangeName"), bean);
                }
            }
        }
        return null;
    }


    private void checkAndThrowCoexist(boolean result){
        Assert.isTrue(
                !result
                ,MessageFormat.format("{0} and {1} cannot coexist"
                        , ConsumerListener.class.getName()
                        ,FanoutConsumerListener.class.getName()
                ));
    }

    //fanout
    private void declareAndBind(String queueName, String exchangeName, Object o){
        String exchangeNameN = exchangeName;
        if (exchangeNameN == null)
            exchangeName = "com.generate.fanout";
        if (queueName.equals("")){
            queueName = QueueBuilder.durable().build().getName();
        }
        Queue queue = QueueBuilder.durable(queueName).autoDelete().build();
        Exchange exchange = ExchangeBuilder.fanoutExchange(exchangeName).autoDelete().build();
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareBinding(BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("")
                .noargs()
        );
        addQueueNames(queueName);
        addQueueToObjectItem(queueName, o);
    }

    private void declareAndBind(String queueName, String exchangeName, String routeKey, Object o){
        String exchangeNameN = exchangeName;
        if (exchangeNameN == null)
            exchangeName = "com.generate.fanout";
        Queue queue = QueueBuilder.durable(queueName).autoDelete().build();
        Exchange exchange = ExchangeBuilder.fanoutExchange(exchangeName).autoDelete().build();
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(routeKey)
                .noargs()
        );
        addQueueNames(queueName);
        addQueueToObjectItem(queueName, o);
    }

    private void declareAndBind(String queueName, Object o){
        amqpAdmin.declareBinding(BindingBuilder
                .bind(QueueBuilder.durable(queueName).autoDelete().build())
                .to((FanoutExchange) ExchangeBuilder.fanoutExchange("com.generate").autoDelete().build())
        );
        addQueueNames(queueName);
        addQueueToObjectItem(queueName, o);
    }

    /**
     * 监听容器增加队列 name
     * @param queueName
     */
    private void addQueueNames(String queueName){
        listenerContainer.addQueueNames(queueName);
    }

    /**
     * 新增队列与监听handler对应关系
     * @param queueName
     * @param o 监听handler，继承AisService
     */
    private void addQueueToObjectItem(String queueName, Object o){
        consumerConfig.addQueueToObjectItem(queueName, o);
    }
}
