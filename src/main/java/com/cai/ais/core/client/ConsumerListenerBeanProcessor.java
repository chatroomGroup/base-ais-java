package com.cai.ais.core.client;

import com.cai.ais.AisService;
import com.cai.ais.MessageExchangeType;
import com.cai.ais.annotation.ConsumerListener;
import com.cai.ais.annotation.FanoutConsumerListener;
import com.cai.ais.annotation.TopicConsumerListener;
import com.cai.ais.core.AisData;
import com.cai.ais.core.exception.AisException;
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
import java.util.ArrayList;
import java.util.List;
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
        try {
            if (bean.getClass().isAnnotationPresent(ConsumerListener.class)
                    || bean.getClass().isAnnotationPresent(FanoutConsumerListener.class)
                    || bean.getClass().isAnnotationPresent(TopicConsumerListener.class)){
                //检查并存问题
                checkAndThrowCoexist(bean, new ArrayList<Class>(){{
                    add(ConsumerListener.class);
                    add(FanoutConsumerListener.class);
                    add(TopicConsumerListener.class);
                }});
                if (bean instanceof AisService){
                    if(bean.getClass().isAnnotationPresent(ConsumerListener.class)){
                        ConsumerListener listener = bean.getClass().getAnnotation(ConsumerListener.class);
                        Map values = AnnotationUtils.getAnnotationAttributes(ConsumerListener.class,listener);
                        declareAndBind((String) values.get("queue"), (String) values.get("exchangeName"), bean);
                    }else if (bean.getClass().isAnnotationPresent(TopicConsumerListener.class)){
                        TopicConsumerListener listener = bean.getClass().getAnnotation(TopicConsumerListener.class);
                        Map values = AnnotationUtils.getAnnotationAttributes(TopicConsumerListener.class,listener);
                        declareAndBind((String) values.get("queue"), (String) values.get("exchangeName"), (String) values.get("routeKey"), bean);
                    }
                }
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }


    private void checkAndThrowCoexist(Object o, List<Class> list){
        List<String> result = new ArrayList<>();
        String str = o.getClass().getName() + " cannot coexist : \n\r\t";
        final Integer[] len = {-1};
        list.forEach(item ->{
            if (o.getClass().isAnnotationPresent(item)){
                result.add(item.getName());
                len[0]++;
            }
        });
        for (int i = 0 ; i <= len[0] ; i++){
            str +="--- {" + i + "}\n\r\t";
        }
        Assert.isTrue(!(len[0] > 0) ,MessageFormat.format(str,result.toArray()));
    }

    //fanout
    private void declareAndBind(String queueName, String exchangeName, Object o) throws AisException {
        String exchangeNameN = exchangeName;
        if (exchangeNameN == null)
            exchangeName = "com.generate.fanout";
        if (queueName.equals("")){
            queueName = QueueBuilder.nonDurable().build().getName();
        }
        Queue queue = QueueBuilder.nonDurable(queueName).autoDelete().build();
        Exchange exchange = AisData.addAndReturnExchange(exchangeName,amqpAdmin,MessageExchangeType.FANOUT);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("")
                .noargs()
        );
        addQueueNames(queueName);
        addQueueToObjectItem(queueName, o);
    }

    //topic
    private void declareAndBind(String queueName, String exchangeName, String routeKey, Object o) throws AisException {
        String exchangeNameN = exchangeName;
        if (exchangeNameN == null)
            exchangeName = "com.generate.topic";
        if (queueName.equals("")){
            queueName = QueueBuilder.nonDurable().build().getName();
        }
        Queue queue = QueueBuilder.nonDurable(queueName).autoDelete().build();
        Exchange exchange = AisData.addAndReturnExchange(exchangeName,amqpAdmin,MessageExchangeType.TOPIC);
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
