package com.atquigu.rabbitmq.six;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * Routing(direct)：路由模式
 */
public class DirectLogs {
    public static final String EXCHANGE_NAME="direct_logs";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

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
            channel.basicPublish(EXCHANGE_NAME,"info",null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息："+message);
        }
    }

}
