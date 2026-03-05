package com.seckill.core.stock.model;


import lombok.Data;
import java.util.List;

/**
 * 库存扣减请求
 */
@Data
public class StockDeductRequest {

    /**
     * 请求ID（用于幂等）
     */
    private String requestId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 扣减项列表
     */
    private List<Item> items;

    @Data
    public static class Item {
        /**
         * 秒杀活动ID
         */
        private Long seckillId;

        /**
         * 商品ID
         */
        private Long productId;

        /**
         * 扣减数量
         */
        private Integer quantity;

        /**
         * 备注
         */
        private String remark;
    }
}
