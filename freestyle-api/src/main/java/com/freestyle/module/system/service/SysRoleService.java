package com.freestyle.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freestyle.module.system.domain.SysRole;

/**
 * @author zhangshichang
 * @date 2019/8/30 下午3:37
 */
public interface SysRoleService extends IService<SysRole> {

    int deleteRole(Integer roleId);
}
