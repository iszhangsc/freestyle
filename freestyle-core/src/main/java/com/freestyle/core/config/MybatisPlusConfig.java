package com.freestyle.core.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源配置
 *
 * @author zhangshichang
 * @date 2019/8/27 上午11:21
 */
@Configuration
@MapperScan(value = {"com.freestyle.module.**.mapper"})
public class MybatisPlusConfig {


    /**
     * 分页插件
     * @return  PaginationInterceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
