package com.atquigu.rabbitmq.fore;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认方式：
 * 1、单个确认模式
 * 2、批量确认
 * 3、异步批量确认
 */
public class ConfirmMessage {
    //批量发布消息的个数
    public static final int MESSAGE_COUNT=1000;

    public static void main(String[] args) throws Exception{

        //1、单个确认模式
        ConfirmMessage.publishMessageIndividual();

        //2、批量确认
        ConfirmMessage.publishMessageBatch();

        //3、异步批量确认
        ConfirmMessage.publicMessageAsync();

    }
    //单个发布确认
    public static void publishMessageIndividual() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare("",true,false,false,null);
        //开启发布确认
        channel.confirmSelect();

        //开始时间
        long begin = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message=i+"";
            channel.basicPublish("",queueName,null,message.getBytes("UTF-8"));
            //单个消息就马上进行发布确认
            boolean flag = channel.waitForConfirms();
            if (flag){
                System.out.println("消息发布成功");
            }
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT +"个单独确认消息，耗时"+(end-begin)+"ms");
    }

    //批量发布确认
    public static void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare("",true,false,false,null);
        //开启发布确认
        channel.confirmSelect();

        //开始时间
        long begin = System.currentTimeMillis();

        //批量确认消息大小
        int batchSize=100;

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message=i+"";
            channel.basicPublish("",queueName,null,message.getBytes("UTF-8"));

            //判断达到100条消息的时候批量确认一次
            if (i%batchSize==0){
                //发布确认
                channel.waitForConfirms();
            }
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT +"个批量确认消息，耗时"+(end-begin)+"ms");
    }

    //异步发布确认
    public static void publicMessageAsync() throws Exception{

        Channel channel = RabbitMqUtils.getChannel();

        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare("",true,false,false,null);
        //开启发布确认
        channel.confirmSelect();

        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况下
         * 1、能轻松的 将序号与消息进行关联
         * 2、轻松批量删除条目，只要给到序号
         * 3、支持高并发
         */
        ConcurrentSkipListMap<Long,Object> outstandingConfirms =new ConcurrentSkipListMap<>();


        //发消息之前准备一个消息监听器，监听哪些消息成功了，哪些消息失败了,两参
        //1、消息确认“成功”回调函数
        ConfirmCallback  ackCallBack = (deliveryTag,multiple)->{
            if (multiple){
                //2、删除掉已经确认的消息，剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, Object> confirmedMessage = outstandingConfirms.headMap(deliveryTag);
                confirmedMessage.clear();
            }else {
                outstandingConfirms.remove(deliveryTag);
            }

            System.out.println("确认的消息的tag："+deliveryTag);

        };
        //2、消息确认"失败"回调函数
        /**
         *  第一个参数是消息的标记（编号）
         * 第二个参数是是否为批量确认
         */
        ConfirmCallback nackCallBack = (deliveryTag,multiple)->{
            //3、打印一下未确认的消息都有哪些
            String message = outstandingConfirms.get(deliveryTag).toString();
            System.out.println("未确认的消息是："+message+"::::::未确认的消息："+deliveryTag);
        };
        /**
         * addConfirmListener(ackCallBack,nackCallBack)：
         * 第一个参数监听哪些消息成功了
         * 第二个参数监听哪些消息失败了
         */
        channel.addConfirmListener(ackCallBack,nackCallBack); //异步通知

        //开始时间
        long begin = System.currentTimeMillis();

        //批量发布消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());

            //1、记录下所有要发送的消息
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
            System.out.println("发送消息完成");
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT +"个异步发布确认消息，耗时"+(end-begin)+"ms");
    }


}
