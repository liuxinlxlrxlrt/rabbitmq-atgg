package com.atquigu.rabbitmq.three;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 队列持久化、消息持久化
 * 生产者
 *
 * 实验目标：消费在手动应答时不丢失，放回队列中重新消费
 */
public class Task2 {

    //队列名称
    public static final String TASK_QUEUE_NAME="ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();

        //信道开启发布确认
        channel.confirmSelect();

        /**
         * 生产一个队列
         * 第一个参数：队列名称
         * 第二个参数：队列里面的消息是否持久化（磁盘）默认情况下消息保存在内存中
         * 第三个参数：该队列是否只供一个消费者进行消费，是否进行消息共享，true可以多个消费者消费
         * 第四个参数：最后一个消费者端开连接以后，该队列是否自动删除，true自动删除，false不自动删除
         * 第五个参数：其他参数
         */
        boolean durable=true;
        channel.queueDeclare(TASK_QUEUE_NAME,durable,false,false,null);

        //从控制台中输入消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();

            /**
             * 发送一个消息
             * 第一个参数：发送到哪个交换机，可以不写
             * 第二个参数：路由key值是哪个，本次队列的名称
             * 第三个参数：其他参数信息
             * 第四个参数：发送的消息的消息体
             */
            //设置生产者发送消息持久化消息（要求保存到磁盘上）保存在内存中
            channel.basicPublish("",TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息："+message);
        }
    }
}
