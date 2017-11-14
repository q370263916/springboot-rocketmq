package com.hice.demo.springboot.rocketmq.order;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author <a href="mailto:hecc@ifengmao.com">hecc</a>
 * @version 1.0
 */
public class OrderConsumer {

    private final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    private DefaultMQPushConsumer consumer;
    private String namesrvAddr = "192.168.1.177:9876";
    private String consumerGroup = "OrderConsumerGroup";

    /**
     * Spring bean init-method
     */
    public void init() throws InterruptedException, MQClientException {

        // 参数信息
        logger.info("OrderConsumer initialize!");
        logger.info(consumerGroup);
        logger.info(namesrvAddr);

        // 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
        // 注意：ConsumerGroupName需要由应用来保证唯一
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(String.valueOf(System.currentTimeMillis()));

        // 订阅指定MyTopic下tags等于MyTag（表达式）
        consumer.subscribe("OrderTopic", "*");

        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 设置为集群消费(区别于广播消费)
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 消费多线程设置
        consumer.setConsumeThreadMin(10);
        consumer.setConsumeThreadMax(20);

        // 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            // 设置自动提交
            context.setAutoCommit(true);
            // 第二个线程无法访问这个队列
            //AtomicLong consumeTimes = new AtomicLong(0);
            try {
                //模拟业务消息处理时间
                Thread.sleep(new Random().nextInt(1000));
                Message msg = msgs.get(0);
                logger.info("顺序消费" + msg + ",内容：" + new String(msg.getBody(), "UTF-8"));
            } catch (InterruptedException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });

        // Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
        consumer.start();

        logger.info("OrderConsumer start success!");
    }

    /**
     * Spring bean destroy-method
     */
    public void destroy() {
        consumer.shutdown();
    }

    // ----------------- setter --------------------

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

}
