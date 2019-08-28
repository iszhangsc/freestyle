package com.freestyle.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 转换工具包
 * @author zhangshichang
 * @date 2019/8/28 上午11:40
 */
public class BaseConvertUtils {


    /**
     * 获取类的所有属性，包括父类
     *
     * @param object 参数对象
     * @return 字段数组
     * @author zhangshichang
     * @date 2019/8/27 上午10:04
     */
    public static Field[] getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * Object对象空判断
     *
     * @param object 参数
     * @return boolean
     * @author zhangshichang
     * @date 2019/8/27 上午10:20
     */
    public static boolean isNotEmpty(Object object) {
        return object != null && !"".equals(object) && !"null".equals(object);
    }

}
