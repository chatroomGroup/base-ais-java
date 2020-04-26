# base-ais-java
## 引言
  此项目主要用于企业级开发的消息队列中间件，内部使用暂时为rabbitMq。采用rabbitmq的原因有以下几点：
  - erlang语言开发，性能极其好，延时很低
  - 吞吐量到万级，MQ功能比较完备  
  - 开源提供的管理界面非常棒，用起来很好用  
  
  当然也有很多不足的地方，比如以下：
  - 吞吐量会低一些，这是因为他做的实现机制比较重
  - erlang开发，很难去看懂源码，基本职能依赖于开源社区的快速维护和修复bug  
  
  总而言之，如果是中小型公司的业务量，吞吐量为万级的rabbit是完全能够支持的。  
## 项目介绍
  该项目使用springboot做快速开发，主要使用以下的包作为支撑。  
  ![mavenBarImage](https://github.com/chatroomGroup/base-ais-java/blob/v1.1/images/maven_jars.jpg)  
  
  springboot依赖的**spring-boot-starter-parent**版本为**2.2.2.RELEASE**，使用自定义的配置，mq相关配置编辑在**application-dev.yml**,格式为  
  ```
ais.mq:
  host: localhost # connection host
  port: 5672 # 5772 # connection port
  username: guest
  password: guest
  concurrency: 1
  maxConcurrency: 1
  virtualHost: /cr
  ``` 
  此外，exchange使用的**autoDelete = true**，采用持久化的策略。queue采用用完随机删除的策略。支持广播(Fanout),路由(Topic)的消息投送方式。
  采用异步的监听策略，用法十分简便。
## 用法
  ### v1.1 and relases
   #### 事件定义 Lisenter  
   定义一个**监听事件**，继承[AisService.java](https://github.com/chatroomGroup/base-ais-java/blob/v1.1/src/main/java/com/cai/ais/AisService.java),
   实现**process**方法，参数msg为[AisMessage.java](https://github.com/chatroomGroup/base-ais-java/blob/v1.1/src/main/java/com/cai/ais/AisMessage.java)的子类,
   打上以下相关注解  
   - **ConsumerListener.java** （默认为Fanout）  
   
   - **FanoutConsumerListener.java** （默认为Fanout）  
   
   - **TopicConsumerListener.java** （默认为Topic）    
   
   **只能取其一，否则会出现异常**。process内部则为监听事件具体的代码。msg则为传送的数据。  
   #### 发送方式定义 sender  
   发送的方法很简单，注入[AisSend.java](https://github.com/chatroomGroup/base-ais-java/blob/v1.1/src/main/java/com/cai/ais/core/send/AisSend.java),
   然后使用send方法发送相关数据。不需要定义queue，所有的queue都是用完即删的。当`routeKey=""`时，默认为Fanout类型交换机，否则为Topic类型。
  
