package com.atquigu.rabbitmq.eight;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

/**
 * 死信队列：消息TTL过期（消息存活时间）
 */
public class Producer {
    //普通交换机
    public static final String NORMAL_EXCHAGE="normal_exchange";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //死信消息，设置TTL(time to live)时间,10000ms=10s
        AMQP.BasicProperties properties =
                new AMQP.BasicProperties().builder().expiration("10000").build();

        for (int i = 1; i < 11; i++) {
            String message = "info:"+i;
            channel.basicPublish(NORMAL_EXCHAGE,"zhangsan",properties,message.getBytes());
        }

    }

}
