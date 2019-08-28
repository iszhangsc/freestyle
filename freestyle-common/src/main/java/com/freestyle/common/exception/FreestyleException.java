package com.freestyle.common.exception;

import com.freestyle.common.enums.ResultEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangshichang
 * @date 2019/8/26 下午5:38
 */
@Setter
@Getter
public class FreestyleException extends RuntimeException {
    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String msg;

    public FreestyleException() {
        super();
    }

    /**
     * 自定义构造函数
     * @param code 状态码
     * @param msg 消息
     */
    public FreestyleException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 自定义构造函数
     * @param resultEnum {@link ResultEnum} 结果枚举
     */
    public FreestyleException(ResultEnum resultEnum) {
        this.msg = resultEnum.getMessage();
        this.code = resultEnum.getCode();
    }
}
