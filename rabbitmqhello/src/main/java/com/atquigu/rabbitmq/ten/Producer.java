package com.atquigu.rabbitmq.ten;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

/**
 * 死信队列：消息被拒绝（basic.reject或者basic.nack）并且requeue=false
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
