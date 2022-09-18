package com.atquigu.rabbitmq.nine;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
/**
 * 死信队列：队列达到最大长度（队列满了，无法再添加数据到mq中）
 */
public class Consumer02 {

    public static final String DEAD_QUEUE="dead_queue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        System.out.println("等待接收消息。。。。。。");

        DeliverCallback deliverCallback=(consumerTag, message)->{
            System.out.println("Consumer02控制台打印接收到的消息："+new String(message.getBody(),"UTF-8"));
        };

        CancelCallback cancelCallback =(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消息接口回调逻辑");
        };
        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,cancelCallback);
    }
}
