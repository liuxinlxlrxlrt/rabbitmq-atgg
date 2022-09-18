package com.atquigu.rabbitmq.seven;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;
/**
 * Topics：主题模式,生产者
 */
public class EmitLogTopic {
    //定义交换机
    public static final String EXCHAGE_NAME="topic_logs";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * quick.orange.rabbit  ：被队列Q1Q2接收到
         * lazy.orange.elephant：被队列Q1Q2接收到
         * quick.orange.fox：被队列Q1接收到
         * lazy.brown.fox：被队列Q2接收到
         * lazy.pink.rabbit：虽然满足两个绑定但是只被队列Q1接收一次
         * quick.brown.fox：不匹配任何绑定不会被任何队列接收道会被丢弃
         * quick.orange.male.rabbit：是四个单词不匹配任何绑定会被丢弃
         * lazy.orange.male.rabbit：是四个单词但匹配Q2
         */
        Map<String,String> bindingKeyMap= new HashMap<>();
        bindingKeyMap.put("quick.orange.rabbit","被队列Q1Q2接收到");
        bindingKeyMap.put("lazy.orange.elephant","被队列Q1Q2接收到");
        bindingKeyMap.put("quick.orange.fox","被队列Q1接收到");
        bindingKeyMap.put("lazy.brown.fox","被队列Q2接收到");
        bindingKeyMap.put("lazy.pink.rabbit","虽然满足两个绑定但是只被队列Q1接收一次");
        bindingKeyMap.put("quick.brown.fox","不匹配任何绑定不会被任何队列接收道会被丢弃");
        bindingKeyMap.put("quick.orange.male.rabbit","是四个单词不匹配任何绑定会被丢弃");
        bindingKeyMap.put("lazy.orange.male.rabbit","是四个单词但匹配Q2");
        for (Map.Entry<String, String> bindingKeyEntry : bindingKeyMap.entrySet()) {
            String routingKey = bindingKeyEntry.getKey();
            String message = bindingKeyEntry.getValue();
            channel.basicPublish(EXCHAGE_NAME,routingKey,null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息："+message);
        }
    }
}
