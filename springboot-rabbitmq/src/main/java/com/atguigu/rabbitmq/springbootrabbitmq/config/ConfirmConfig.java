package com.atguigu.rabbitmq.springbootrabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发布确认（高级内容）
 */
@Configuration
public class ConfirmConfig {
    //交换机
    public static final String CONFIRM_EXCAHNGE_NAME="confirm_exchange";

    //队列
    public static final String CONFIRM_QUEUE_NAME="confirm_queue";

    //routingkey
    public static final String CONFIRM_ROUTING_KEY="key1";

    //备份交换机
    public static final String BACKUP_EXCAHNGE_NAME="backup_exchange";

    //备份队列
    public static final String BACKUP_QUEUE_NAME="backup_queue";

    //报警队列
    public static final String WARNING_QUEUE_NAME="warning_queue";

    //声明交换机
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
        //正常交换声明写法
//      return new DirectExchange(CONFIRM_EXCAHNGE_NAME);
        //一旦确认交换机无法投递消息时，转发给备份交换机
        return ExchangeBuilder.directExchange(CONFIRM_EXCAHNGE_NAME).durable(true)
                .withArgument("alternate-exchange",BACKUP_EXCAHNGE_NAME).build();
    }

    //声明队列
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    //绑定
    @Bean
    public Binding queueBindingExchange(@Qualifier("confirmQueue") Queue confirmQueue,
                                        @Qualifier("confirmExchange") DirectExchange confirmExchange){

        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }

    //声明备份交换机
    @Bean("backupExchange")
    public FanoutExchange backupExchange(){

        return  new FanoutExchange(BACKUP_EXCAHNGE_NAME);
    }

    @Bean("backupQueue")
    public Queue backupQueue(){

        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    @Bean("warningQueue")
    public Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    @Bean
    public Binding backQueueBindingBackupExchange(@Qualifier("backupQueue") Queue backupQueue,
                                                  @Qualifier("backupExchange") FanoutExchange backupExchange){
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }

    @Bean
    public Binding warninQueueBindingBackupExchange(@Qualifier("warningQueue") Queue warningQueue,
                                                    @Qualifier("backupExchange") FanoutExchange backupExchange){

        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }

}
