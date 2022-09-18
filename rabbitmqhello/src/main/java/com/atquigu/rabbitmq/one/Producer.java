package com.atquigu.rabbitmq.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

//生产者
public class Producer {
    private static  final String QUEUE_NAME="hello";

    //发消息
    public static void main(String[] args) throws Exception {
        //创建一个链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP 连接rabbitmq工厂队列
        factory.setHost("127.0.0.1");
        factory.setUsername("admin");
        factory.setPassword("123456");

        //创建连接
        Connection connection = factory.newConnection();

        //获取信道
       Channel channel= connection.createChannel();

       //采用默认交换机，声明一个队列
        /**
         * 生产一个队列
         * 第一个参数：队列名称
         * 第二个参数：队列里面的消息是否持久化（磁盘）默认情况下消息保存在内存中
         * 第三个参数：该队列是否只供一个消费者进行消费，是否进行消息共享，true可以多个消费者消费
         * 第四个参数：最后一个消费者端开连接以后，该队列是否自动删除，true自动删除，false不自动删除
         * 第五个参数：其他参数
         *
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //准备发消息
        String message = "hello world";
        //发消息
        /**
         * 发送一个消息
         * 第一个参数：发送到哪个交换机，可以不写
         * 第二个参数：路由key值是哪个，本次队列的名称
         * 第三个参数：其他参数信息
         * 第四个参数：发送的消息的消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
        System.out.println("发送消息完毕");

    }
}
