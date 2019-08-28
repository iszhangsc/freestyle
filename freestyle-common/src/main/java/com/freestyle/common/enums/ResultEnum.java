package com.freestyle.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回结果枚举
 * @author zhangshichang
 * @date 2019/8/26 下午5:11
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    /**
     * 成功
     */
    SUCCESS(200, "成功"),
    /**
     * 错误
     */
    ERROR(500, "服务器内部错误");

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 失败消息
     * @see ResultEnum
     */
    private String message;
}
