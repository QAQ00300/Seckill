package com.seckill.core.mq.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendResult {
    private String messageId;
    private String topic;
    private boolean success;
    private String errorMsg;


}
