package com.freestyle.core.shiro;

import com.google.common.collect.Maps;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义Shiro配置
 *
 * @author zhangshichang
 * @date 2019/8/27 下午2:31
 */
@Configuration
public class ShiroConfiguration {


    /**
     * Shiro生命周期处理器
     * <p>LifecycleBeanPostProcessor，这是个DestructionAwareBeanPostProcessor的子类，
     * 负责org.apache.shiro.util.Initializable 类型Bean的生命周期的，初始化和销毁。
     * 主要是AuthorizingRealm类的子类，以及EhCacheManager类。</p>
     * @return  LifecycleBeanPostProcessor
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * HashedCredentialsMatcher，这个类是为了对密码进行编码的，
     * 防止密码在数据库里明码保存，当然在登陆认证的时候，
     * 这个类也负责对form里输入的密码进行编码。
     */
///*    @Bean(name = "hashedCredentialsMatcher")
//    public HashedCredentialsMatcher hashedCredentialsMatcher() {
//        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
//        credentialsMatcher.setHashAlgorithmName("MD5");
//        credentialsMatcher.setHashIterations(2);
//        credentialsMatcher.setStoredCredentialsHexEncoded(true);
//        return credentialsMatcher;
//    }*/



    /**
     * securityManager 不用直接注入shiroDBRealm，可能会导致事务失效
     * 解决方法见 handleContextRefresh
     * http://www.debugrun.com/a/NKS9EJQ.html
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(TokenRealm tokenRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(tokenRealm);

        /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-
         * StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        // todo 设置自定义cache
        return securityManager;
    }

    /**
     * ShiroFilterFactoryBean 是一个factoryBean,为了生成ShiroFilter
     * 它主要保存了三项数据, securityManager filters, filterChainDefinitionMap
     * @author zhangshichang
     * @date 2019/8/30 下午5:20
     * @param securityManager  安全管理
     * @return  ShiroFilterFactoryBean
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        //cas验证登录
        filterChainDefinitionMap.put("/cas/client/validateLogin", DefaultFilter.anon.name());
        // 配置不会被拦截的链接 顺序判断
        //登录接口排除
        filterChainDefinitionMap.put("/sys/login", DefaultFilter.anon.name());
        // druid 监控页面排除
        filterChainDefinitionMap.put("/druid/**", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/sys/needNotLogin", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.js", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.css", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.html", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.svg", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.pdf", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.jpg", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.png", DefaultFilter.anon.name());
        filterChainDefinitionMap.put("/**/*.ico", DefaultFilter.anon.name());
        //登出接口排除
        filterChainDefinitionMap.put("/sys/logout", DefaultFilter.anon.name());
        // 添加自己的过滤器并且取名为jwtFilter
        Map<String, Filter> filterMap = Maps.newHashMap();
        filterMap.put("jwtFilter", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        // <!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        filterChainDefinitionMap.put("/**", "jwtFilter");

        // 未授权界面返回JSON
        shiroFilterFactoryBean.setLoginUrl("/sys/common/notLogin");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    /**
     * 开启Shiro的注解:如<code>@RequiresRoles</code>,<code>@RequiresPermissions</code>,需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return DefaultAdvisorAutoProxyCreator
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }


    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
