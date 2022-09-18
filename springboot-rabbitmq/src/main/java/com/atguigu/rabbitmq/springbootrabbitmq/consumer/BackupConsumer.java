package com.atguigu.rabbitmq.springbootrabbitmq.consumer;

import com.atguigu.rabbitmq.springbootrabbitmq.config.ConfirmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 备份消费者
 */
@Component
public class BackupConsumer {
    private static final Logger logger  = LoggerFactory.getLogger(Consumer.class);

    @RabbitListener(queues = ConfirmConfig.BACKUP_QUEUE_NAME)
    public void receiveBackupMsg(Message message){
        String msg = new String(message.getBody());
        logger.info("备份交换机发现不可交换info消息：{}",msg);
        logger.error("备份交换机发现不可交换error消息：{}",msg);
    }
}
