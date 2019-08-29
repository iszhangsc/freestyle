package com.freestyle.common.vo;

import com.freestyle.common.enums.ResultEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 所有接口返回数据格式
 *
 * @author zhangshichang
 * @date 2019/8/26 下午4:53
 */
@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 4579402522540563301L;

    /**
     * 成功标志
     */
    private Boolean success;

    /**
     * 返回处理消息
     */
    private String message;

    /**
     * 返回代码
     */
    private Integer code;

    /**
     * 返回数据对象 data
     */
    private T result;

    /**
     * 时间戳
     */
    private Date timestamp;

    /**
     * 请求路由
     */
    private String path;

    private ResultVO() {

    }

    /**
     * 有数据返回构造函数
     * @param success   成功标识
     * @param message   返回处理消息
     * @param code      返回代码
     * @param result    返回数据对象
     * @param timestamp 时间戳
     */
    public ResultVO(Boolean success, String message, Integer code, T result, Date timestamp) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.result = result;
        this.timestamp = timestamp;
    }

    /**
     * 无数据返回构造函数
     * @param success   成功标识
     * @param message   返回处理消息
     * @param code      返回代码
     * @param timestamp 时间戳
     */
    private ResultVO(Boolean success, String message, Integer code, Date timestamp) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

    /**
     * 无数据返回构造函数
     * @param success   成功标识
     * @param message   返回处理消息
     * @param code      返回代码
     * @param timestamp 时间戳
     */
    private ResultVO(Boolean success, String message, Integer code, Date timestamp, String path) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
        this.path = path;
    }



    /**
     * 成功并且有数据返回调用
     *
     * @param t 返回JSON对象
     * @return ResultVo
     */
    public static <T> ResultVO<T> ok(T t) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(ResultEnum.SUCCESS.getMessage());
        result.setResult(t);
        result.setSuccess(true);
        result.setTimestamp(new Date());
        return result;
    }

    /**
     * 成功无据返回调用
     *
     * @return ResultVo
     */
    public static <T> ResultVO<T> ok() {
        return new ResultVO<>(true, ResultEnum.SUCCESS.getMessage(), ResultEnum.SUCCESS.getCode(), new Date());
    }

    /**
     * 失败返回错误信息调用
     *
     * @return ResultVo
     */
    public static <T> ResultVO<T> fail(ResultEnum resultEnum) {
        return new ResultVO<>(false, ResultEnum.SUCCESS.getMessage(), ResultEnum.SUCCESS.getCode(), new Date());
    }

    /**
     * 失败返回错误信息调用
     *
     * @return ResultVo
     */
    public static <T> ResultVO<T> fail(String message) {
        return new ResultVO<>(false, message, 400, new Date());
    }

    /**
     * 失败返回错误信息调用
     *
     * @return ResultVo
     */
    public static <T> ResultVO<T> fail(int code, String message) {
        return new ResultVO<>(false, message, code, new Date());
    }

    /**
     * 失败返回错误信息调用
     *
     * @return ResultVo
     */
    public static <T> ResultVO<T> fail(int code, String message, String path) {
        return new ResultVO<>(false, message, code, new Date(), path);
    }

    /**
     * 失败调用
     *
     * @return ResultVO
     */
    public static  <T> ResultVO<T> error(String message, String path) {
        return new ResultVO<>(false, message, ResultEnum.SUCCESS.getCode(), new Date(), path);
    }

}

