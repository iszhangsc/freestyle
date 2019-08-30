package com.freestyle.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freestyle.module.system.domain.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限表 Mapper 接口
 *
 * @author zhangshichang
 * @date 2019/8/30 上午10:36
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<SysPermission> queryByUsername(@Param("username") String username);
}
