package com.atguigu.rabbitmq.springbootrabbitmq.consumer;

import com.atguigu.rabbitmq.springbootrabbitmq.config.DelayedQueueConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 延迟队列消费者 基于插件的延迟消息
 */
@Component
public class DelayQueueConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DelayQueueConsumer.class);

    //监听消息
    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
    public void receiveDelayQueue(Message message){
        String msg = new String(message.getBody());
        logger.info("当前时间：{}，收到延迟队列的消息：{}",new Date().toString(),msg);
    }
}
