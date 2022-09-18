package com.atguigu.rabbitmq.springbootrabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 发布确认回调接口实现类
 * <p>
 * MyCallBack implements RabbitTemplate.ConfirmCallback
 * 继承或者实现的是一个类（对象）的内部接口，实现类并不在对象里面，会导致将来调用不到内部接口
 * 解决办法：应该把实现类注入到对象的内部接口中，才能调用到内部接口，才能调用到实现类
 *
 *
 * 现在A类中成员变量b被@Autowried注解，实例b注入是发生在A的构造方法执行完之后的。
 * 如果在A的构造方法中需要用到实例b，那么就无法在构造函数中进行；此时可以采用@PostConstruct注解，
 * @PostConstruct注解的作用是在依赖注入完成后调用被注解的方法；
 *
 * 要想回调接口实现类生效：
 *必须在配置文件中加上spring.rabbitmq.publisher-confirm-type：correlated
 *
 * spring.rabbitmq.publisher-confirm-type:
 * 1、NONE:禁用发布确认模式，默认值
 * 2、correlated ：发布确认成功到交换机后会触发回调方法
 * 3、SEMPLE:同步确认消息，效果和correlated一样，
 *          waitConfirm或者waitConfirmOrDie方法等待broker节点返回发送消息，
 *          根据返回结果类判断下一步逻辑，
 *          waitConfirmOrDie方法如果返回为false则会关闭channel，则接下来无法发送消息到broker
 *
 */
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    private static final Logger logger = LoggerFactory.getLogger(MyCallBack.class);

    //第二步
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //第三步@PostConstruct是在其他注解实现完成之后再实现
    @PostConstruct
    public void init() {
        //注入：this为当前类
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 交换机确认回调方法：
     * 1、发消息，交换机收到了 回调
     *
     * @param correlationData 保存回调消息的ID以及相关信息，
     *                        从发送消息的rabbitTemplate.convertAndSend()来的，需自己手动填写
     * @param b               ： 交换机收到消息 ack=true
     * @param s：原因            null
     *                        <p>
     *                        交换机确认回调方法：
     *                        1、发消息，交换机接收失败了 回调
     * @param correlationData 保存回调消息的ID以及相关嘻嘻
     * @param b               ： 交换机没有收到 ack=false
     * @param s：              失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";

        if (ack) {
            logger.info("交换机已经收到Id为：{}的消息", id);
        } else {
            logger.info("交换机还未收到Id为：{}的消息，由于原因：{}", id,cause);
        }
    }

    //可以在当新消息传递过程中不可达目的地时，将消息返回给生产者
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        logger.error("ReturnedMessage: " + returnedMessage);
        System.out.println("----------------------------------------------------");
        logger.error("消息：{}，被交换机'{}'退回,退回原因：{}，路由key：{} " + returnedMessage.getMessage()
                ,returnedMessage.getExchange(),returnedMessage.getReplyText(),returnedMessage.getRoutingKey());
    }
}
