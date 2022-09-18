package com.atquigu.rabbitmq.six;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * Routing(direct)：路由模式
 */
public class ReceiveLogsDirect02 {
        public static final String EXCHANGE_NAME="direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        channel.queueDeclare("disk",false,false,false,null);

        channel.queueBind("disk",EXCHANGE_NAME,"error");

        //消息接收
        DeliverCallback deliverCallback =(consumerTag, message)->{
            System.out.println("ReceiveLogsDirect02控制台打印接收到的消息："+new String(message.getBody(),"UTF-8"));
        };

        //消费者取消消息时回调接口
        CancelCallback cancelCallback =(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消息接口回调逻辑");
        };

        //消费者接收消息
        channel.basicConsume("disk",true,deliverCallback,cancelCallback);
    }

}
