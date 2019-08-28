package com.freestyle.core.config;

import com.freestyle.common.constant.DataBaseConstant;
import com.freestyle.common.util.BaseConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

/**
 * 自定义Mybatis拦截器，自动注入创建人、创建时间、修改人、修改时间
 * @author zhangshichang
 * @date 2019/8/27 上午9:50
 */
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class CustomizeMybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        final String sqlId = mappedStatement.getId();
        log.debug("-----------------sqlId----------------:{}", sqlId);
        final SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        log.debug("-----------------sqlCommandType----------------:{}", sqlCommandType);
        Object parameter = invocation.getArgs()[1];

        if (parameter == null) {
            return invocation.proceed();
        }

        insert(sqlCommandType, parameter);

        update(sqlCommandType, parameter);
        // 放行
        return invocation.proceed();
    }

    /**
     * 更新注入操作
     * @param sqlCommandType    SQL语句类型
     * @param parameter         参数
     * @throws IllegalAccessException 反射异常
     */
    private void update(SqlCommandType sqlCommandType, Object parameter) throws IllegalAccessException {
        if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
            Field[] fields = null;
            if (parameter instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) parameter;
                if (paramMap.containsKey("et")) {
                    parameter = paramMap.get("et");
                } else {
                    parameter = paramMap.get("param1");
                }
                fields = BaseConvertUtils.getAllFields(parameter);
            } else {
                fields = BaseConvertUtils.getAllFields(parameter);
            }

            for (Field field : fields) {
                final String fieldName = field.getName();
                log.debug("-----------------field.name----------------:{}", fieldName);
                if (DataBaseConstant.UPDATE_BY.equals(field.getName())) {
                    field.setAccessible(true);
                    Object localUpdateBy = field.get(parameter);
                    field.setAccessible(false);
                    if (localUpdateBy == null || "".equals(localUpdateBy)) {
                        String updateBy = "none";
                        // 获取登录用户信息
                        final Object user = SecurityUtils.getSubject().getPrincipal();
                        if (user != null) {
                            // 登录账号
                            updateBy = "YYY";
                        }
                        if (BaseConvertUtils.isNotEmpty(updateBy)) {
                            field.setAccessible(true);
                            field.set(parameter, updateBy);
                            field.setAccessible(false);
                        }
                    }
                }

                if (DataBaseConstant.UPDATE_TIME.equals(field.getName())) {
                    field.setAccessible(true);
                    Object localUpdateTime = field.get(parameter);
                    field.setAccessible(false);
                    if (localUpdateTime == null) {
                        field.setAccessible(true);
                        field.set(parameter, new Date());
                        field.setAccessible(false);
                    }
                }
            }
        }
    }

    /**
     * 插入注入操作
     * @param sqlCommandType    SQL语句类型
     * @param parameter         参数
     * @throws IllegalAccessException 反射异常
     */
    private void insert(SqlCommandType sqlCommandType, Object parameter) throws IllegalAccessException {
        if (SqlCommandType.INSERT.equals(sqlCommandType)) {
            final Field[] fields = BaseConvertUtils.getAllFields(parameter);
            for (Field field : fields) {
                final String fieldName = field.getName();
                log.debug("-----------------field.name----------------:{}", fieldName);
                // todo 获取登录用户信息
//                final SysUser user = (SysUser)SecurityUtils.getSubject().getPrincipal();

                // ----------------------------------------------------------- 注入创建人 -------------------------------------------- //
                if (DataBaseConstant.CREATE_BY.equals(fieldName)) {
                    field.setAccessible(true);
                    final Object localCreateBy = field.get(parameter);
                    field.setAccessible(false);
                    if (localCreateBy == null || "".equals(localCreateBy)) {
                        String createBy = "none";
//                        if (user != null) {
//                            // todo 登录账号 user.getUsername()
//                            createBy = user.getUsername();
//                        }
                        field.setAccessible(true);
                        field.set(parameter, createBy);
                        field.setAccessible(false);
                    }
                }

                // ----------------------------------------------------------- 注入创建时间 -------------------------------------------- //
                if (DataBaseConstant.CREATE_TIME.equals(fieldName)) {
                    field.setAccessible(true);
                    final Object localCreateTime = field.get(parameter);
                    field.setAccessible(false);
                    if (localCreateTime == null) {
                        field.setAccessible(true);
                        field.set(parameter, new Date());
                        field.setAccessible(false);
                    }
                }

                // todo 此处还可以继续注入,看具体的业务
            }
        }
    }

    /**
     * 把这个拦截器生成一个代理放到拦截器链中
     * @param target    目标对象
     * @return  Object
     */
    @Override
    public Object plugin(Object target) {
        // Mybatis 官方推荐写法
        return Plugin.wrap(target, this);
    }

    /**
     * 插件初始化的时候调用，也只调用一次，插件配置的属性从这里设置进来
     * @param properties    插件配置的属性
     */
    @Override
    public void setProperties(Properties properties) {
        log.info("自定义Mybatis拦截器成功.............");
    }

}
