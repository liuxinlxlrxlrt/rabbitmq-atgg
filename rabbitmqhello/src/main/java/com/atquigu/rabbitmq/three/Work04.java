package com.atquigu.rabbitmq.three;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.atquigu.rabbitmq.utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 队列持久化、消息持久化、手动应答
 * 实验目标：消费在手动应答时不丢失，放回队列中重新消费
 */
public class Work04 {

    public static final String TASK_QUEUE_NAME="ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        System.out.println("C2等待接收消息处理时间较长");

        DeliverCallback deliverCallback =(consumerTag,message)->{
            //沉睡30s
            SleepUtils.sleep(30);
            System.out.println("接收到的消息："+new String(message.getBody(),"UTF-8"));

            //手动应答
            /**
             * 第一个参数：表示消息的标记 tag
             * 第二个参数：是否批量应答,false不批量，true不批量
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        //消息接收被取消时，执行下面的内容
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消息接口回调逻辑");
        };

        //设置不公平分发
        int perfetchCount= 1;//1就是不公平分发的方式
        channel.basicQos(perfetchCount);

        //采用手动应答
        boolean autoAck= false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
