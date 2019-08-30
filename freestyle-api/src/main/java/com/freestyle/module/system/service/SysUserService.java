package com.freestyle.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freestyle.module.system.domain.SysUser;

import java.util.Set;

/**
 * @author zhangshichang
 * @date 2019/8/27 下午2:56
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 通过用户名获取用户信息
     * @param username  用户名
     * @return  SysUser
     */
    SysUser getUserByName(String username);

    /**
     * 通过用户名获取用户拥有的角色集合
     * @param username  用户名
     * @return  角色集合
     */
    Set<String> getUserRolesSet(String username);

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    Set<String> getUserPermissionsSet(String username);
}
