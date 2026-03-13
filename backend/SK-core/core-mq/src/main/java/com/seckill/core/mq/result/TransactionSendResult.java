package com.seckill.core.mq.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 事务发送结果
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSendResult extends SendResult {
    private String transactionId;
    private String transactionState;
    /**
    TransactionSendResult 可能需要更多的初始化逻辑，或者其字段不能简单通过 setter 方法赋值。
    此时需要显式定义构造方法来确保对象创建时的完整性。
    */
    public TransactionSendResult(String messageId, String topic, boolean success, String errorMsg, String s, String commit) {
    }
}
