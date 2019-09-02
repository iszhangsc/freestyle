package com.freestyle.core.shiro;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.freestyle.common.constant.CacheConstant;
import com.freestyle.common.util.JwtUtil;
import com.freestyle.core.component.RedisComponent;
import com.freestyle.core.component.SpringContextComponent;
import com.freestyle.module.system.domain.SysUser;
import com.freestyle.module.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * TokenRealm，这是个自定义的认证类，继承自AuthorizingRealm， 负责用户的认证和权限的处理.
 *
 * @author zhangshichang
 * @date 2019/8/27 下午2:42
 */
@Slf4j
@Component
public class TokenRealm extends AuthorizingRealm {

    private final SysUserService sysUserService;
    private final RedisComponent redisComponent;

    /**
     * 此处一定要懒加载，否则 sysUserService事物、缓存都不生效
     * @author zhangshichang
     * @date 2019/9/2 上午10:52
     */
    @Lazy
    public TokenRealm(RedisComponent redisComponent, SysUserService sysUserService) {
        this.redisComponent = redisComponent;
        this.sysUserService = sysUserService;
    }


    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 功能： 获取用户权限信息，包括角色以及权限。只有当触发检测用户权限时才会调用此方法，
     * 例如checkRole,checkPermission
     *
     * @param principals token
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.debug("————权限认证 [ roles、permissions]————");
        SysUser sysUser = null;
        String username = null;
        if (principals != null) {
            sysUser = (SysUser) principals.getPrimaryPrincipal();
            username = sysUser.getUsername();
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 设置用户拥有的角色集合，比如“admin,test”
        // todo 该接口实现后续可以添加缓存策略,避免频繁认证
        Set<String> roleSet = sysUserService.getUserRolesSet(username);
        info.setRoles(roleSet);

        // 设置用户拥有的权限集合，比如“sys:role:add,sys:user:add”
        // todo 该接口实现后续可以添加缓存策略,避免频繁认证
        Set<String> permissionSet = sysUserService.getUserPermissionsSet(username);
        info.addStringPermissions(permissionSet);
        return info;
    }

    /**
     * 功能： 用来进行身份认证，也就是说验证用户输入的账号和密码是否正确，获取身份验证信息，错误抛出异常
     *
     * @param auth 用户身份信息 token
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        if (token == null) {
            log.warn("————————身份认证失败——————————IP地址:  " + IpUtils.getIpAddrByRequest(SpringContextComponent.getHttpServletRequest()));
            throw new AccountException("token为空!");
        }
        // 校验token有效性
        SysUser loginUser = this.checkUserTokenIsEffect(token);
        return new SimpleAuthenticationInfo(loginUser, token, getName());
    }

    /**
     * 校验token的有效性
     *
     * @param token token
     */
    private SysUser checkUserTokenIsEffect(String token) throws AuthenticationException {
        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token非法无效!");
        }

        SysUser sysUser = sysUserService.getUserByName(username);
        if (sysUser == null) {
            throw new AuthenticationException("用户不存在!");
        }

        // 校验token是否超时失效 & 或者账号密码是否错误
        if (!jwtTokenRefresh(token, username, sysUser.getPassword())) {
            throw new AuthenticationException("Token失效，请重新登录!");
        }

        return sysUser;
    }

    /**
     * JWTToken刷新生命周期 （解决用户一直在线操作，提供Token失效问题）
     * 1、登录成功后将用户的JWT生成的Token作为k、v存储到cache缓存里面(这时候k、v值一样)
     * 2、当该用户再次请求时，通过JWTFilter层层校验之后会进入到doGetAuthenticationInfo进行身份验证
     * 3、当该用户这次请求JWTToken值还在生命周期内，则会通过重新PUT的方式k、v都为Token值，缓存中的token值生命周期时间重新计算(这时候k、v值一样)
     * 4、当该用户这次请求jwt生成的token值已经超时，但该token对应cache中的k还是存在，则表示该用户一直在操作只是JWT的token失效了，程序会给token对应的k映射的v值重新生成JWTToken并覆盖v值，该缓存生命周期重新计算
     * 5、当该用户这次请求jwt在生成的token值已经超时，并在cache中不存在对应的k，则表示该用户账户空闲超时，返回用户信息已失效，请重新登录。
     * 6、每次当返回为true情况下，都会给Response的Header中设置Authorization，该Authorization映射的v为cache对应的v值。
     * 7、注：当前端接收到Response的Header中的Authorization值会存储起来，作为以后请求token使用
     * 参考方案：https://blog.csdn.net/qq394829044/article/details/82763936
     *
     * @param userName 用户名
     * @param passWord 密码
     * @return boolean
     */
    private boolean jwtTokenRefresh(String token, String userName, String passWord) {
        String cacheToken = String.valueOf(redisComponent.get(CacheConstant.USER_TOKEN_CACHE_PREFIX + token));
        if (StringUtils.isNotEmpty(cacheToken)) {
            // 校验token有效性
            if (!JwtUtil.verify(cacheToken, userName, passWord)) {
                String newAuthorization = JwtUtil.sign(userName, passWord);
                redisComponent.set(CacheConstant.USER_TOKEN_CACHE_PREFIX + token, newAuthorization);
                // 设置超时时间
                redisComponent.expire(CacheConstant.USER_TOKEN_CACHE_PREFIX + token, JwtUtil.EXPIRE_TIME / 1000);
            } else {
                redisComponent.set(CacheConstant.USER_TOKEN_CACHE_PREFIX + token, cacheToken);
                // 设置超时时间
                redisComponent.expire(CacheConstant.USER_TOKEN_CACHE_PREFIX + token, JwtUtil.EXPIRE_TIME / 1000);
            }
            return true;
        }
        return false;
    }

}
