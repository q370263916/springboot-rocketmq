package com.hice.demo.springboot.rocketmq.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;

/**
 * @author <a href="mailto:hecc@ifengmao.com">hecc</a>
 * @version 1.0
 */
public class OrderProducer {

    private final Logger logger = LoggerFactory.getLogger(OrderProducer.class);

    private DefaultMQProducer producer;
    private String producerGroup = "OrderProducerGroup";
    private String namesrvAddr = "192.168.1.177:9876";

    /**
     * Spring bean init-method
     */
    public void init() throws MQClientException {
        // 参数信息
        logger.info("OrderProducer initialize!");
        logger.info(producerGroup);
        logger.info(namesrvAddr);

        // 初始化
        producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(String.valueOf(System.currentTimeMillis()));
        producer.setRetryTimesWhenSendFailed(3);

        producer.start();

        logger.info("OrderProducer start success!");
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
