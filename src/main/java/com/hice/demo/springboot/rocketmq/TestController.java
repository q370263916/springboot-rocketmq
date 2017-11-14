package com.hice.demo.springboot.rocketmq;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.hice.demo.springboot.rocketmq.normal.MyProducer;
import com.hice.demo.springboot.rocketmq.order.OrderProducer;
import com.hice.demo.springboot.rocketmq.transaction.TransactionExecuterImpl;
import com.hice.demo.springboot.rocketmq.transaction.TransactionProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyson
 * @create 2017-03-17 14:23
 */
@RestController
@RequestMapping("/v1/mq")
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private MyProducer myProducer;
    @Autowired
    private OrderProducer orderProducer;
    @Autowired
    private TransactionProducer transactionProducer;

    /**
     * 普通无序消息
     *
     * @param msg
     */
    @GetMapping("/test")
    public void sendMessage(String msg) {
        Message message = new Message("MyTopic", "MyTag", (JSONObject.toJSONString(msg)).getBytes());
        SendResult sendResult = null;
        try {
            sendResult = myProducer.getProducer().send(message, 1000);
        } catch (RemotingException | MQBrokerException | InterruptedException | MQClientException e) {
            logger.debug("异常类型{},异常信息{}", "RemotingException", e.getMessage() + String.valueOf(sendResult));
        }
        // 当消息发送失败时如何处理
        if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
            logger.info("消息发送失败");
        }
    }

    /**
     * 顺序消息
     *
     * @param msg
     */
    @GetMapping("/order")
    public void sendOrderMessage(String msg) {
        String[] tags = new String[] { "createTag", "payTag", "sendTag" };

        for (int orderId = 0; orderId <= 10; orderId++) {
            for (int type = 0; type < 3; type++) {
                SendResult sendResult = null;
                try {
                    Message message = new Message("OrderTopic", tags[type % tags.length], orderId + ":" + type,
                            (orderId + ":" + type + msg).getBytes());
//                    sendResult = orderProducer.getProducer().send(message, new MessageQueueSelector() {
//                        @Override
//                        public MessageQueue select(List<MessageQueue> mqs, Message message, Object args) {
//                            Integer id = (Integer) args;
//                            int index = id % mqs.size();
//                            return mqs.get(index);
//                        }
//                    }, orderId);
                    sendResult = orderProducer.getProducer().send(message, (mqs, message1, args) -> {
                        Integer id = (Integer) args;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }, orderId);
                } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info(sendResult.toString());
            }
        }
        orderProducer.getProducer().shutdown();
    }

    /**
     * 事务消息
     *
     * @param msg
     */
    @GetMapping("/transaction")
    public void sendTransactionMessage(String msg) {
        TransactionExecuterImpl transactionExecuter = new TransactionExecuterImpl();
        try {
            Message message1 = new Message("TransactionTopic", "TransactionTag1", "KEY1", ("hello rmq 1" + msg).getBytes());

            Message message2 = new Message("TransactionTopic", "TransactionTag2", "KEY2", ("hello rmq 2" + msg).getBytes());

            SendResult sendResult1 = transactionProducer.getProducer()
                    .sendMessageInTransaction(message1, transactionExecuter, null);
            logger.info(new Date() + "message1: " + sendResult1);

            SendResult sendResult2 = transactionProducer.getProducer()
                    .sendMessageInTransaction(message2, transactionExecuter, null);
            logger.info(new Date() + "message2: " + sendResult2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        transactionProducer.getProducer().shutdown();
    }

}
