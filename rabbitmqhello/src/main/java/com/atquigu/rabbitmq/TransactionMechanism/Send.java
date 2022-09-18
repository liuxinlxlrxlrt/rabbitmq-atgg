package com.atquigu.rabbitmq.TransactionMechanism;

import com.atquigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {
    public static final String TRANSACTIONEXCHANGE="transactionExchange";
    public static final String TRANSACTIONQUEUE="transactionQueue";
    public static final String TRANSACTIONTROUTINGKEY="transactionRoutingKey";

    public static void main(String[] args) throws Exception {
        Channel channel =null;
        try {
            channel = RabbitMqUtils.getChannel();
            channel.queueDeclare(TRANSACTIONQUEUE,false,false,false,null);
            channel.exchangeDeclare(TRANSACTIONEXCHANGE,"direct",true);
            channel.queueBind(TRANSACTIONQUEUE,TRANSACTIONEXCHANGE,TRANSACTIONTROUTINGKEY);

            //启动事务：启动事务后所有写入到队列的消息
            //必须显示为txCommit提交事务或者txRollback回滚事务
            channel.txSelect();
            for (int i = 1; i < 10; i++) {
                String msg = "transaction的测试消息！-"+i;
                channel.basicPublish(TRANSACTIONEXCHANGE,TRANSACTIONTROUTINGKEY,null,msg.getBytes("UTF-8"));
//            System.out.println(10/0);
//            channel.basicPublish(TRANSACTIONEXCHANGE,TRANSACTIONTROUTINGKEY,null,msg.getBytes("UTF-8"));

            }

            //提交事务，如果调用txSelect()方法启动了事务，必须显示调用事务的提交，否则消息不会真正的写入到队列中
            //提交事务后，会将内存中的消息写入队列，并释放内存
            channel.txCommit();
            System.out.println("消息发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            if (channel!=null){
                try {
                    //回滚事务，当前事务中所有没有提交的消息，释放内存
                    channel.txRollback();
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
