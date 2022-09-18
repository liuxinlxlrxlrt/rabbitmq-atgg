package com.atguigu.rabbitmq.springbootrabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * TTL队列，配置文件类
 */
@Configuration
public class TTLQueueConfig {
    //普通交换机的名称
    public static final String X_EXCHANGE="X";

    //死信交换机名称
    public static final String Y_DEAD_LETTER_EXCHANGE="Y";

    //普通队列名称
    public static final String QUEUE_A="QA";
    public static final String QUEUE_B="QB";

    //死信队列名称
    public static final String DEAD_LETTER_QUEUE="QD";

    //普通队列名称
    public static final String QUEUE_C="QC";

    //声明QC
    @Bean("queueC")
    public Queue queueC(){
        Map<String,Object> arguments = new HashMap<>(3);
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        arguments.put("x-dead-letter-routing-key","YD");
        //TTL(不写ttl时间就是适合于所有时间)
//        arguments.put("x-message-ttl",3600000);
        return QueueBuilder.durable(QUEUE_C).withArguments(arguments).build();
    }

    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }


    //声明普通交换机
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    //声明死信交换机
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //声明队列
    @Bean("queueA")
    public Queue queueA(){
        Map<String,Object> arguments=new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL存活时间(单位是ms)
        arguments.put("x-message-ttl",10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(arguments).build();
    }

    @Bean("queueB")
    public Queue queueB(){
        Map<String,Object> arguments=new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL存活时间(单位是ms)
        arguments.put("x-message-ttl",40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(arguments).build();
    }


    //死信队列
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    //绑定
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xEchange){
        return BindingBuilder.bind(queueA).to(xEchange).with("XA");
    }

    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB,
                                  @Qualifier("xExchange") DirectExchange xEchange){
        return BindingBuilder.bind(queueB).to(xEchange).with("XB");
    }

    @Bean
    public Binding queueDBindingY(@Qualifier("queueD") Queue queueD,
                                  @Qualifier("yExchange") DirectExchange yEchange){
        return BindingBuilder.bind(queueD).to(yEchange).with("YD");
    }


}
