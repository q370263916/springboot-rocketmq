package com.hice.demo.springboot.rocketmq.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * 未决事务，服务器回查客户端
 *
 * @author <a href="mailto:hecc@ifengmao.com">hecc</a>
 * @version 1.0
 */
public class TransactionCheckListenerImpl implements TransactionCheckListener {

    private final Logger logger = LoggerFactory.getLogger(TransactionCheckListenerImpl.class);

    /**
     * 主动检查机制
     *
     * @param messageExt
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransactionState(MessageExt messageExt) {
        logger.info("主动检查机制:" + messageExt.toString());
        // 由于RMQ迟迟没有收到确认消息，会主动询问这条prepare消息，是否正常？
        // 可以查询数据库看这个消息是否已经处理

        // 这种消息意味着事务将不会被消费者读取到 ROLLBACK_MESSAGE UNKNOW throw Exception
        return LocalTransactionState.COMMIT_MESSAGE;
    }

}
