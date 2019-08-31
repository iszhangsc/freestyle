package com.freestyle.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freestyle.module.system.domain.SysUserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色表 Mapper 接口
 *
 * @author zhangshichang
 * @date 2019/8/30 上午9:58
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 获取用户角色编码
     * @param username 角色名
     * @return  角色编码集合
     */
    @Select("select role_code from t_sys_role where id in (select role_id from t_sys_user_role where user_id = (select id from t_sys_user where username=#{username}))")
    List<String> getRoleByUsername(@Param("username") String username);

}
