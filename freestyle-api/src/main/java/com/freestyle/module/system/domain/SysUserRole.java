package com.freestyle.module.system.domain;

import com.freestyle.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户角色关联表
 * @author zhangshichang
 * @date 2019/8/30 上午9:55
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysUserRole extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1063857024906267827L;

    /**
     * 用户表主键
     */
    private Integer userId;

    /**
     * 角色表主键
     */
    private Integer roleId;

}
