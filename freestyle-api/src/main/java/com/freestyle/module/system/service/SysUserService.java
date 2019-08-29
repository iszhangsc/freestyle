package com.freestyle.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freestyle.module.system.domain.SysUser;

/**
 * @author zhangshichang
 * @date 2019/8/27 下午2:56
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getUserByName(String username);
}
