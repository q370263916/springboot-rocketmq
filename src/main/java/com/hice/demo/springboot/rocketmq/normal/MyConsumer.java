package com.hice.demo.springboot.rocketmq.normal;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author hyson
 * @create 2017-03-17 14:22
 */
public class MyConsumer {

    private final Logger logger = LoggerFactory.getLogger(MyConsumer.class);

    private DefaultMQPushConsumer consumer;
    private String namesrvAddr = "192.168.1.177:9876";
    private String consumerGroup = "MyConsumerGroup";

    /**
     * Spring bean init-method
     */
    public void init() throws InterruptedException, MQClientException {

        // 参数信息
        logger.info("MyConsumer initialize!");
        logger.info(consumerGroup);
        logger.info(namesrvAddr);

        // 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
        // 注意：ConsumerGroupName需要由应用来保证唯一
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(String.valueOf(System.currentTimeMillis()));

        // 订阅指定MyTopic下tags等于MyTag（表达式）
        consumer.subscribe("MyTopic", "MyTag");

        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 设置为集群消费(区别于广播消费)
        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {

            MessageExt msg = msgs.get(0);
            try {
                if (msg.getTopic().equals("MyTopic")) {
                    // TODO 执行Topic的消费逻辑
                    logger.info("执行Topic的消费逻辑");
                    if (msg.getTags() != null && msg.getTags().equals("MyTag")) {
                        // TODO 执行Tag的消费
                        logger.info("执行Tag的消费");
                        try {
                            logger.info("普通消费" + msg + ",内容：" + new String(msg.getBody(), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                if (msg.getReconsumeTimes() == 3) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            // 如果没有return success ，consumer会重新消费该消息，直到return success
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
        consumer.start();

        logger.info("MyConsumer start success!");
    }

    public void destroy() {
        consumer.shutdown();
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

}
