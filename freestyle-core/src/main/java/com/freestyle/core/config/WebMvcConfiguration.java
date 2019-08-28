package com.freestyle.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhangshichang
 * @date 2019-08-28 22:33
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${freestyle.path.upload}")
    private String upLoadPath;
    @Value("${freestyle.path.webapp}")
    private String webAppPath;
    @Value("${spring.resources.static-locations}")
    private String staticLocations;

    /**
     * 静态资源的配置 - 使得可以从磁盘中读取 Html、图片、视频、音频等
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + upLoadPath + "//", "file:" + webAppPath + "//")
                .addResourceLocations(staticLocations.split(","));
    }

}
