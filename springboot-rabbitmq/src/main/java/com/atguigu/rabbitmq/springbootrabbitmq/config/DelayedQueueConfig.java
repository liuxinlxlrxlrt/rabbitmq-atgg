package com.atguigu.rabbitmq.springbootrabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class DelayedQueueConfig {
    public static final String DELAYED_QUEUE_NAME="dalayed.queue";

    public static final String DALAYED_EXCHANGE_NAME="delayed.exchange";

    public static final String DELAYED_ROUTING_KEY="dalayed.routingkey";

    //声明队列
    @Bean
    public Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME);
    }

    //声明交换机(基于插件)
    @Bean
    public CustomExchange delayedExchange(){
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type","direct");

        /**
         * 1、交换机的名称
         * 2、交换机的类型
         * 3、是否需要持久化
         * 4、是否需要自动删除
         * 5、参数
         */

        return new CustomExchange(DALAYED_EXCHANGE_NAME,"x-delayed-message",
                true,false,arguments);
    }

    //绑定
    @Bean
    public Binding delayQueueBindingDelayedExchange(@Qualifier("delayedQueue") Queue delayedQueue,
                                                    @Qualifier("delayedExchange") CustomExchange delayedExchange){

        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }
}
