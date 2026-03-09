package com.seckill.core.seckill.dto;



import lombok.Data;
import java.math.BigDecimal;

/**
 * 秒杀响应DTO
 */
@Data
public class SeckillResponse {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 秒杀活动ID
     */
    private Long seckillId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 总金额
     */
    private BigDecimal totalPrice;

    /**
     * 处理耗时（毫秒）
     */
    private Long processTime;

    /**
     * 响应时间
     */
    private Long responseTime = System.currentTimeMillis();


    public static SeckillResponse success(String orderNo, BigDecimal seckillPrice) {
        SeckillResponse response = new SeckillResponse();
        response.setSuccess(true);
        response.setOrderNo(orderNo);
        response.setSeckillPrice(seckillPrice);
        return response;
    }

    /**
     * 失败响应
     */
    public static SeckillResponse fail(Integer errorCode, String errorMessage) {
        SeckillResponse response = new SeckillResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode != null ? String.valueOf(errorCode) : "500");
        response.setErrorMessage(errorMessage);
        return response;
    }

    /**
     * 失败响应（字符串错误码）
     */
    public static SeckillResponse fail(String errorCode, String errorMessage) {
        SeckillResponse response = new SeckillResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }
}