### v1.1-reply  
  该版本新增Receive功能，consumer可以返回一个***Object类型***参数，server采用***同步方式***进行获取返回值。  
  direct方式则直接获得消费者的参数，当一个交换机对应多个队列时，系统采用获取最后一个可获得的值返回给server，代码中如下：
  ```  
  template.setUseDirectReplyToContainer(false);  
  ```  
  结果如下  
  ![images](https://github.com/chatroomGroup/base-ais-java/blob/v1.1/images/1.1-reply_result.jpg)  
  
  该版本中再监听事件中改动很大，在[ConsumerConfiguration.java](https://github.com/chatroomGroup/base-ais-java/blob/v1.1_reply/src/main/java/com/cai/ais/core/client/ConsumerConfiguration.java),
  直接更改了设置监听的方法，之前采用重写***MessageListener***类的***onMessage***方法。添加reply功能时发现该方法只能简单的消费消息，不能进行回复以及其他操作。
  现更改为使用MessageListenerAdapter类的方法，delegate的设置手动获取填充。直接触发Listener类则交由适配器类来处理。
  代码如下：  
  ```
   @Bean
    public SimpleMessageListenerContainer listenerContainer(MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(aisProperties.getConcurrency());
        container.setMaxConcurrentConsumers(aisProperties.getMaxConcurrency());
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setReceiveTimeout(10000);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                AisService ais = (AisService) queueToObject.get(message.getMessageProperties().getConsumerQueue());
                log.info(MessageFormat.format("exchange: [ {0} ,routeKey: {1} ] is executing",message.getMessageProperties().getReceivedExchange(),message.getMessageProperties().getReceivedRoutingKey()));
                listenerAdapter.setDelegate(ais);
                listenerAdapter.setDefaultListenerMethod("process");
                listenerAdapter.onMessage(message, channel);
            }
        });
        return container;
    }
```

note: 版本彻底更新后，该分支会合并到v1.2
