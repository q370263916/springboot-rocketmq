package com.hice.demo.springboot.rocketmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.hice.demo.springboot.rocketmq.normal.MyConsumer;
import com.hice.demo.springboot.rocketmq.normal.MyProducer;
import com.hice.demo.springboot.rocketmq.order.OrderConsumer;
import com.hice.demo.springboot.rocketmq.order.OrderProducer;
import com.hice.demo.springboot.rocketmq.transaction.TransactionConsumer;
import com.hice.demo.springboot.rocketmq.transaction.TransactionProducer;

/**
 * @author <a href="mailto:hecc@ifengmao.com">hecc</a>
 * @version 1.0
 */
@Configuration
@ComponentScan("com.hice.demo.springboot.rocketmq")
public class ConsumerConfig {

    /**
     * 普通消息
     *
     * @return
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    MyConsumer myConsumer() {
        return new MyConsumer();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    MyProducer myProducer() {
        return new MyProducer();
    }

    /**
     * 顺序消息
     *
     * @return
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    OrderConsumer orderConsumer() {
        return new OrderConsumer();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    OrderProducer orderProducer() {
        return new OrderProducer();
    }

    /**
     * 事务消息
     *
     * @return
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    TransactionConsumer transactionConsumer() {
        return new TransactionConsumer();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    TransactionProducer transactionProducer() {
        return new TransactionProducer();
    }

}
