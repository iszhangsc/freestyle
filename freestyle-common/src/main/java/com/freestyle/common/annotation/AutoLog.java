package com.freestyle.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统日志注解
 *
 * @author zhangshichang
 * @date 2019/8/26 下午5:22
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLog {

    /**
     * 日志内容
     * @return  String
     */
    String content() default "";

    /**
     * 日志类型
     * @return  int 0:操作日志;1:登录日志;2:定时任务;
     */
    int logType() default 0;

    /**
     * 操作日志类型
     * @return  int （1查询，2添加，3修改，4删除）
     */
    int operateType() default 0;
}
