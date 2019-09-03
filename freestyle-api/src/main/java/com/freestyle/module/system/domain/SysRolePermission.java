package com.freestyle.module.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.freestyle.core.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 角色权限表
 * @author zhangshichang
 * @date 2019/8/30 上午10:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_sys_role_permission")
public class SysRolePermission extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 4668224286375721932L;

    private Integer roleId;

    private Integer permissionId;
}
