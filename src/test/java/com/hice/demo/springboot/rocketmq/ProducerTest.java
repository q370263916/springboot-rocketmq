package com.hice.demo.springboot.rocketmq;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

/**
 * @author hyson
 * @create 2017-03-17 15:35
 */
public class ProducerTest {


    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("Producer");
        producer.setNamesrvAddr("192.168.1.177:9876");
        try {
            producer.start();

            Message msg = new Message("PushTopic",
                    "push",
                    "1",
                    "Just for test.".getBytes());

            SendResult result = producer.send(msg);
            System.out.println("1-id:" + result.getMsgId() +
                    " result:" + result.getSendStatus());

            msg = new Message("PushTopic",
                    "push",
                    "2",
                    "Just for test.".getBytes());

            result = producer.send(msg);
            System.out.println("2-id:" + result.getMsgId() +
                    " result:" + result.getSendStatus());

            msg = new Message("PullTopic",
                    "pull",
                    "1",
                    "Just for test.".getBytes());

            result = producer.send(msg);
            System.out.println("3-id:" + result.getMsgId() +
                    " result:" + result.getSendStatus());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }

}
