package com.atquigu.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    //芮烈名称

    public static final String QUEUE_NAME="hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("admin");
        factory.setPassword("123456");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        //接收消息
        DeliverCallback deliverCallback = (consumerTag,message)->{
            System.out.println(new String(message.getBody()));
        };

        CancelCallback cancelCallback = consumerTag->{
            System.out.println("消息消费被中断");
        };

        /**
         *消费者消费消息
         * 第一个参数：消费哪个队列
         * 第二个参数：消费成功后是否自动应答，true 代表自动应答，false 手动弄应答
         * 第三个参数：消费者未消费消费的回调
         * 第四个参数：消费者取消消费的回调
         */

        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
