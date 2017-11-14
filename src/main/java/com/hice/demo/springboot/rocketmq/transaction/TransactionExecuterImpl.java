package com.hice.demo.springboot.rocketmq.transaction;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;

/**
 * 执行本地事务
 *
 * @author <a href="mailto:hecc@ifengmao.com">hecc</a>
 * @version 1.0
 */
public class TransactionExecuterImpl implements LocalTransactionExecuter {
    private final Logger logger = LoggerFactory.getLogger(TransactionExecuterImpl.class);

    /**
     * 执行本地业务逻辑
     *
     * @param message
     * @param arg
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message message, Object arg) {
        try {
            //DB操作 应该带上事务service -> dao
            //如果数据库操作失败 需要回滚 同时 返回RMQ一个失败消息 意味消费者将无法消费到这条失败的消息
            //如果成功 需要告诉RMQ一个成功消息，意味消费者将读取到消息
            logger.info("执行本地事务msg = " + new String(message.getBody()));
            logger.info("执行本地事务arg = " + arg);
            String tags = message.getTags();

            // 这种消息意味着事务将不会被消费者读取到 ROLLBACK_MESSAGE UNKNOW throw Exception
            if (tags.equals("TransactionTag2")) {
                logger.info("执行本地事务ROLLBACK");
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }

        } catch (Exception e) {
            logger.info(new Date() + "本地事务执行异常");
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        return LocalTransactionState.COMMIT_MESSAGE;
    }

}
