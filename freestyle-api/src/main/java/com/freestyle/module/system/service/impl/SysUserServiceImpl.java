package com.freestyle.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freestyle.module.system.domain.SysUser;
import com.freestyle.module.system.mapper.SysUserMapper;
import com.freestyle.module.system.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * @author zhangshichang
 * @date 2019/8/27 下午2:57
 */
@Service
public class SysUserServiceImpl  extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public SysUser getUserByName(String username) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(lambdaQueryWrapper);
    }

}
