package com.atquigu.rabbitmq.seven;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * Topics：主题模式，消费者C2
 */
public class ReceiveLogsTopk02 {
    //定义交换机
    public static final String EXCHAGE_NAME="topic_logs";

    //接收消息
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHAGE_NAME, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueName="Q2";
        //队列声明
        /**
         * 生产一个队列
         * 第一个参数：队列名称
         * 第二个参数：队列里面的消息是否持久化（磁盘）默认情况下消息保存在内存中
         * 第三个参数：该队列是否只供一个消费者进行消费，是否进行消息共享，true可以多个消费者消费
         * 第四个参数：最后一个消费者端开连接以后，该队列是否自动删除，true自动删除，false不自动删除
         * 第五个参数：其他参数
         */
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName,EXCHAGE_NAME,"*.*.rabbit");
        channel.queueBind(queueName,EXCHAGE_NAME,"lazy.#");
        System.out.println("等待接收消息。。。。。。");

        DeliverCallback deliverCallback =(consumerTag,message)->{
            System.out.println(new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列："+queueName+"，绑定键："+message.getEnvelope().getRoutingKey());
        };

        CancelCallback cancelCallback =(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消息接口回调逻辑");
        };
        //接收消息
        channel.basicConsume(queueName,true,deliverCallback,cancelCallback);
    }
}
