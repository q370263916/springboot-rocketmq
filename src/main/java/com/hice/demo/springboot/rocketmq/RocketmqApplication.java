package com.hice.demo.springboot.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RocketMQ提供了3种模式的Producer：
 * NormalProducer（普通）、OrderProducer（顺序）、TransactionProducer（事务）
 */
@SpringBootApplication
public class RocketmqApplication {

    public static void main(String[] args) {

        SpringApplication.run(RocketmqApplication.class, args);
    }

}
