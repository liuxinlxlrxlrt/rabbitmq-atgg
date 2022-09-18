package com.atguigu.rabbitmq.springbootrabbitmq.controller;

import com.atguigu.rabbitmq.springbootrabbitmq.config.DelayedQueueConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 发送延迟消息
 */
@RestController
@RequestMapping("/ttl")
public class SendMsgController {
    private static final Logger logger = LoggerFactory.getLogger(SendMsgController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //开始发消息
    @GetMapping("/sendMsg/{messge}")
    public void sendMsg(@PathVariable String messge) {
        logger.info("当前时间：{},发送一条消息给两个TTL队列：{}", new Date().toString(), messge);
        rabbitTemplate.convertAndSend("X", "XA", "消息来自TTL为10s的队列：" + messge);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自TTL为40s的队列：" + messge);
    }

    //开始发消息（消息+TTL）
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable("message") String message, @PathVariable("ttlTime") String ttlTIme) {
        logger.info("当前时间：{},发送一条时长{}毫秒TTL消息给队列QC：{}", new Date().toString(), ttlTIme, message);
        rabbitTemplate.convertAndSend("X", "XC", message, msg -> {
            //设置发送消息时候的延迟时长
            msg.getMessageProperties().setExpiration(ttlTIme);
            return msg;
        });
        System.out.println("消息发送完成");
    }

    //开始发消息 基于插件的，消息以及延迟的时间
    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable("message") String message, @PathVariable("delayTime") Integer delayTime) {

        logger.info("当前时间：{},发送一条时长{}毫秒消息给延迟队列delayed.queue：{}", new Date().toString(), delayTime, message);

        rabbitTemplate.convertAndSend(DelayedQueueConfig.DALAYED_EXCHANGE_NAME,
                DelayedQueueConfig.DELAYED_ROUTING_KEY, message, msg -> {
                    msg.getMessageProperties().setDelay(delayTime);
                    return msg;
                });
        System.out.println("消息发送完成");
    }

    //开始发消息，测试确认

}
