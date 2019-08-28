package com.freestyle.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典注解
 * @author zhangshichang
 * @date 2019/8/26 下午5:24
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    /**
     * 数据 code
     * @return  String
     */
    String dictCode();

    /**
     * 方法描述
     * @return  String
     */
    String dictText() default "";

    /**
     * 数据字典表
     * @return  String
     */
    String dictTable() default "";
}
