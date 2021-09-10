package com.cai.ais.config;

import com.cai.ais.core.exception.AisException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.MessageFormat;
import java.util.*;
import static com.cai.ais.utils.ConvertUtil.*;

public class ConsumerProcessProxy<T extends AisService> implements ChannelCreator<T>{

    Logger log = LoggerFactory.getLogger(ConsumerProcessProxy.class);

    T proxy;

    String queue;

    String exchangeName;

    String routeKey;

    BuiltinExchangeType type;

    int channelNum = 1;

    private List<Channel> channelObjs;

    private Map<Integer, Channel> nameToChannel;

    Connection connection;


    public ConsumerProcessProxy(T proxy,
                                Connection connection,
                                String queue,
                                String exchangeName,
                                String routeKey,
                                BuiltinExchangeType type) {
        this.proxy = proxy;
        this.connection = connection;
        this.queue = queue;
        this.exchangeName = exchangeName;
        this.routeKey = routeKey;
        this.type = type;
        afterProperties();
    }

    public ConsumerProcessProxy(T proxy,
                                Connection connection,
                                String queue,
                                String exchangeName,
                                String routeKey,
                                BuiltinExchangeType type,
                                int channelNum) {
        this.proxy = proxy;
        this.connection = connection;
        this.queue = queue;
        this.exchangeName = exchangeName;
        this.routeKey = routeKey;
        this.type = type;
        this.channelNum = channelNum;
        afterProperties();
    }

    void afterProperties(){
        channelObjs = new LinkedList<>();
        nameToChannel = new HashMap<>();
    }

    public void createChannel(){
        createProxy(proxy,queue,exchangeName,routeKey,type,channelNum);
    }

    @Override
    public ConsumerProcessProxy createProxy(T proxy, String queue, String exchangeName, String routeKey, BuiltinExchangeType type, int channelNum) {
        try {
            Channel channel;
            List<Channel> channels = new ArrayList<>();
            for (int i = 0 ; i < channelNum ; i++){
                channels.add(channel = connection.createChannel(false));
                channel.exchangeDeclare(queue, type,false);
                channel.queueDeclare(queue, true, false,false, null);
                channel.exchangeBind(queue, queue, queue);
                channel.queueBind(queue,queue,queue);
                Consumer consumer = new CustomerConsumer<AisService<AisMessage>>(queue, proxy,  channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        Object o = null;
                        try {
                            o = convertAisMessage(body);
                        } catch (AisException e) {
                            e.printStackTrace();
                        }
                        log.info(MessageFormat.format("consumerTag: {0}", consumerTag));
                        this.server.process((AisMessage) o);
                        log.info("消息处理成功");
                    }

                };
                channel.basicQos(64);
                channel.basicConsume(queue,true, consumer);
                channelObjs.add(channel);
                nameToChannel.put(channel.getChannelNumber(), channel);
            }
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 转换字节数组为AisMessage
     * 失败返回null
     * @param body
     * @return
     */
    AisMessage convertAisMessage(byte[] body) throws AisException, JsonProcessingException {
        try{
            Object o = bytesToObject(body);
            return (AisMessage) o;
        }catch(Exception e){
            if (e instanceof IOException){
                return getJSON().readValue(new String(body), AisMessage.class);
            }
        }
        throw new AisException("转换失败");
    }

    @Override
    public ConsumerProcessProxy getProxy() {
        return this;
    }


    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
        ObjectInputStream ois = new ObjectInputStream (bis);
        obj = ois.readObject();
        ois.close();
        bis.close();
        return obj;
    }

    class CustomerConsumer<T extends AisService> extends DefaultConsumer{

        String queue;

        T server;
        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public CustomerConsumer(String queue, T server, Channel channel) {
            super(channel);
            this.queue = queue;
            this.server = server;
        }
    }
}