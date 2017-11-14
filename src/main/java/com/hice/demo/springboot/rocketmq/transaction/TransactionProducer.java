package com.hice.demo.springboot.rocketmq.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionMQProducer;

/**
 * 事务消息生产者
 *
 * @author <a href="mailto:hecc@ifengmao.com">hecc</a>
 * @version 1.0
 */
public class TransactionProducer {

    private final Logger logger = LoggerFactory.getLogger(TransactionProducer.class);

    private TransactionCheckListener listener;
    private TransactionMQProducer producer;
    private String producerGroup = "TransactionProducerGroup";
    private String namesrvAddr = "192.168.1.177:9876";

    /**
     * Spring bean init-method
     */
    public void init() throws MQClientException {
        // 参数信息
        logger.info("TransactionProducer initialize!");
        logger.info(producerGroup);
        logger.info(namesrvAddr);

        // 初始化
        listener = new TransactionCheckListenerImpl();
        producer = new TransactionMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(String.valueOf(System.currentTimeMillis()));
        producer.setRetryTimesWhenSendFailed(3);

        // 事务回查最小并发数
        producer.setCheckThreadPoolMinSize(2);
        // 事务回查最大并发数
        producer.setCheckThreadPoolMaxSize(2);
        // 队列数
        producer.setCheckRequestHoldMax(2000);
        producer.setTransactionCheckListener(listener);

        producer.start();

        logger.info("TransactionProducer start success!");

    }

    public void destroy() {
        producer.shutdown();
    }

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

}
