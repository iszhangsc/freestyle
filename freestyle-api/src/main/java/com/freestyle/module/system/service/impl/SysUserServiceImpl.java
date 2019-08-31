package com.freestyle.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freestyle.common.constant.CacheConstant;
import com.freestyle.module.system.domain.SysPermission;
import com.freestyle.module.system.domain.SysUser;
import com.freestyle.module.system.mapper.SysPermissionMapper;
import com.freestyle.module.system.mapper.SysUserMapper;
import com.freestyle.module.system.mapper.SysUserRoleMapper;
import com.freestyle.module.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangshichang
 * @date 2019/8/27 下午2:57
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, SysPermissionMapper sysPermissionMapper, SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public SysUser getUserByName(String username) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstant.USER_ROLE_CACHE_PREFIX, key = "'prefix:'+#username")
    public Set<String> getUserRolesSet(String username) {
        final List<String> roles = sysUserRoleMapper.getRoleByUsername(username);
        log.debug("通过数据库读取用户:{}---->拥有的角色:{}", username, roles);
        return new HashSet<>(roles);
    }

    @Override
    public Set<String> getUserPermissionsSet(String username) {
        final List<SysPermission> sysPermissions = sysPermissionMapper.queryByUsername(username);
        log.debug("--------通过数据库读取用户拥有的权限-----------------");
        return sysPermissions.stream().map(SysPermission::getPermissionValue).collect(Collectors.toSet());
    }

}
