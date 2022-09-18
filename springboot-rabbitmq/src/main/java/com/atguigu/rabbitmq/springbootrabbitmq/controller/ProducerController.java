package com.atguigu.rabbitmq.springbootrabbitmq.controller;

import com.atguigu.rabbitmq.springbootrabbitmq.config.ConfirmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.Configuration;

/**
 * 开发发消息，测试确认
 *
 */
@RestController
@RequestMapping("/confirm")
public class ProducerController {
    private static final Logger logger  = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable("message") String message){

        //(1)、消息正确
        //在RabbitTemplate.ConfirmCallback的实现类中引用
        CorrelationData correlationData1 =  new CorrelationData("1");

        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCAHNGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY,message+"key1",correlationData1);

        logger.info("发送消息内容为：{}",message+"key1");
        System.out.println("-------------------------------------");
        //（2）、交换机错误
        //在RabbitTemplate.ConfirmCallback的实现类中引用
        CorrelationData correlationData2 =  new CorrelationData("2");

        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCAHNGE_NAME+"123",
                ConfirmConfig.CONFIRM_ROUTING_KEY,message+"key2",correlationData2);

        logger.info("发送消息内容为：{}",message+"key2");
        System.out.println("-------------------------------------");
        //（3）、信道错误（routing key 错误）
        //在RabbitTemplate.ConfirmCallback的实现类中引用
        CorrelationData correlationData3 =  new CorrelationData("3");

        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCAHNGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY+"123",message+"key3",correlationData3);

        logger.info("发送消息内容为：{}",message+"key3");

    }
}
