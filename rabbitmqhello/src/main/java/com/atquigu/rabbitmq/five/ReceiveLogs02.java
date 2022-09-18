package com.atquigu.rabbitmq.five;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * Publish/Subscribe（fanout）：发布订阅模式
 * 消息接收
 */
public class ReceiveLogs02 {
    public static final String EXCHANGE_NAME="logs";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        //声明一个队列，临时队列
        /**
         * 生成一个临时队列，队列的名称是随机的
         * 当消费者断开与队列的连接的时候，队列就 自动删除
         */
        String queueName=channel.queueDeclare().getQueue();
        /**
         * 绑定交换机和队列
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"");
        System.out.println("等待接收消息，把接收到的消息打印在屏幕上");

        //消息接收
        DeliverCallback deliverCallback =(consumerTag,message)->{
            System.out.println("ReceiveLogs02控制台打印接收到的消息："+new String(message.getBody(),"UTF-8"));
        };

        //消费者取消消息时回调接口
        CancelCallback cancelCallback =(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消息接口回调逻辑");
        };

        //消费者接收消息
        channel.basicConsume(queueName,true,deliverCallback,cancelCallback);


    }

    
}
