package com.atquigu.rabbitmq.nine;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

/**
 * 死信队列：队列达到最大长度（队列满了，无法再添加数据到mq中）
 */
public class Producer {
    //普通交换机
    public static final String NORMAL_EXCHAGE="normal_exchange";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        for (int i = 1; i < 11; i++) {
            String message = "info:"+i;
            channel.basicPublish(NORMAL_EXCHAGE,"zhangsan",null,message.getBytes());
        }

    }

}
