package com.freestyle.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freestyle.common.constant.CacheConstant;
import com.freestyle.module.system.domain.SysRole;
import com.freestyle.module.system.mapper.SysRoleMapper;
import com.freestyle.module.system.service.SysRoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * @author zhangshichang
 * @date 2019/8/30 下午3:38
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    @CacheEvict(value = CacheConstant.USER_ROLE_CACHE_PREFIX, allEntries = true)
    public int deleteRole(Integer roleId) {
        return sysRoleMapper.deleteById(roleId);
    }

}
