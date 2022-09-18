package com.atquigu.rabbitmq.TransactionMechanism;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;

public class Receive {
    public static final String TRANSACTIONEXCHANGE="transactionExchange";
    public static final String TRANSACTIONQUEUE="transactionQueue";
    public static final String TRANSACTIONTROUTINGKEY="transactionRoutingKey";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(TRANSACTIONQUEUE,false,false,false,null);
        channel.exchangeDeclare(TRANSACTIONEXCHANGE,"direct",true);
        channel.queueBind(TRANSACTIONQUEUE,TRANSACTIONEXCHANGE,TRANSACTIONTROUTINGKEY);

        /**
         * 开启事务
         * 当消费者开启事务后，及时不做事务的提交，那么依然可以获取队列中的消息
         * 消息并且将消息从队列中移除掉
         * 注意：
         * 暂时事务队列接收者没有任务影响
         */
        channel.txSelect();
        DeliverCallback deliverCallback=(consumerTag,message)->{

            //获取消息是否被接受过，false表示消息之前没有被接受过，
            //反之为true，表示消息之前被接受过，可因此我们要进行消息的防重复处理
            boolean redeliver = message.getEnvelope().isRedeliver();
            //获取消息的编号
            long deliveryTag = message.getEnvelope().getDeliveryTag();

            if (!redeliver){
                String msg= new String(message.getBody());
                System.out.println( "接收到事务的消息是："+msg+"，消息的编号为:"+deliveryTag);
                System.out.println("获取消息是否被接受过:"+redeliver);
                channel.basicAck(deliveryTag,true);

            }else {
                //程序到了这里表示这个消息已经被接受过，需要进行防重复处理
                //例如查询数据库中是否已经添加了记录，或者修改过了记录
                //如果经过判断这条消息没有被处理完成，则需要重新处理消息，然后确认掉这条消息，
                //如果已经处理过了，则直接确认消息即可，不需要进行其他处理操作
                channel.basicAck(deliveryTag,false);
            }

        };

        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消息接口回调逻辑");
        };
        channel.basicConsume(TRANSACTIONQUEUE,false,deliverCallback,cancelCallback);
        //注意了：如果启动了事务，而消息的消费者确认模式为手动确认，那么必须要提交事务，
        //否则即时调用了确认方法，那么消息也不会从队列中移除
        channel.txCommit();
    }
}
