package com.seckill.core.stock.model;

import lombok.Data;

@Data
public class StockDeductResult {
    /**
    * 请求ID，用于标识本次请求
     */
    private String requestId;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误码（失败时使用）
     */
    private String errorCode;

    /**
     * 错误信息（失败时使用）
     */
    private String errorMessage;


}
