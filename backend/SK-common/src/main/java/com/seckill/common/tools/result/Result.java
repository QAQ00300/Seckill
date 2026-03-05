package com.seckill.common.tools.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果封装类
 * @param <T> 数据类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "统一响应结果")
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    // 成功响应方法
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // 错误响应方法
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    // 常用错误类型
    public static <T> Result<T> paramError(String message) {
        return new Result<>(400, message, null);
    }

    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权", null);
    }

    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问", null);
    }

    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源不存在", null);
    }

    public static <T> Result<T> internalError(String message) {
        return new Result<>(500, message, null);
    }

    // 业务异常
    public static <T> Result<T> businessError(String message) {
        return new Result<>(600, message, null);
    }

    // 判断方法
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}