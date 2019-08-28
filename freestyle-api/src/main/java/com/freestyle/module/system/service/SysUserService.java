package com.freestyle.module.system.service;

import com.freestyle.module.system.domain.SysUser;

/**
 * @author zhangshichang
 * @date 2019/8/27 下午2:56
 */
public interface SysUserService {
    SysUser getUserByName(String username);
}
