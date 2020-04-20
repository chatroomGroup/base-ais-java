package com.cai.ais;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageConsumerConfig implements BeanPostProcessor,ApplicationContextAware {
    public ApplicationContext context;
    public Map<String,Object> queses;
    public List<Queue> queues;
    public Map<String,Object> quesesMethods;
    public List<String> queuesName;
    public Map<String,MessageExchangeType> queuesType;
    public Map<String,String> queuesRoutekeys;
    public static Logger logger = LoggerFactory.getLogger(MessageConsumerConfig.class);

    @PostConstruct
    void init(){
        queses = new HashMap<>();
        queues = new ArrayList<>();
        quesesMethods = new HashMap<>();
        queuesName = new ArrayList<>();
        queuesType = new HashMap<>();
        queuesRoutekeys = new HashMap<>();
        logger = LoggerFactory.getLogger(MessageConsumerConfig.class);
    }

    /*
    搜集上下文中所有已定义好的队列信息
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        BeanFactory beanFactory = (DefaultListableBeanFactory)context.getAutowireCapableBeanFactory();
//        beanFactory.removeBeanDefinition('aisReceiver')
        if (bean.getClass().isAnnotationPresent(MessageEndPoint.class)){
            queses.put((String)bean.getClass().getAnnotation(MessageEndPoint.class).queue() ,beanName);
            queues.add(new Queue(bean.getClass().getAnnotation(MessageEndPoint.class).queue(),false));
            try {
                quesesMethods.put(bean.getClass().getAnnotation(MessageEndPoint.class).queue()
                        ,bean.getClass().getDeclaredMethod("process",AisMessage.class).getName());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            queuesName.add(bean.getClass().getAnnotation(MessageEndPoint.class).queue());
            queuesType.put(bean.getClass().getAnnotation(MessageEndPoint.class).queue(),bean.getClass().getAnnotation(MessageEndPoint.class).exchange());
            queuesRoutekeys.put(bean.getClass().getAnnotation(MessageEndPoint.class).queue(),bean.getClass().getAnnotation(MessageEndPoint.class).routeKey());
//            logger.error('{} is consumer!!!',beanName)
        }else{
//            logger.info('{} is not consumer!!!',beanName)
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Map<String, Object> getQueses() {
        return queses;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
