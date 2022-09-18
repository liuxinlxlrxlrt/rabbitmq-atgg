package com.atquigu.rabbitmq.two;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Work Queues：工作模式
 * 线程（相当于之前的消费者）
 */
public class Work01 {

    //队列名称
    public static final String QUEUE_NAME="hello";

    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();

        //消息的接收
        DeliverCallback deliverCallback =(consumerTag,message)->{
            System.out.println("接收到消息："+new String(message.getBody()));
        };

        //消息接收被取消时，执行下面的内容
        CancelCallback cancelCallback = (consumerTag)->{
            System.out.println(consumerTag+"消费者取消消息接口回调逻辑");
        };

        /**
         *消费者消费消息
         * 第一个参数：消费哪个队列
         * 第二个参数：消费成功后是否自动应答，true 代表自动应答，false 手动弄应答
         * 第三个参数：消费者未消费消费的回调
         * 第四个参数：消费者取消消费的回调
         */
        System.out.println("C2等待接收消息。。。。。。");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }

}
