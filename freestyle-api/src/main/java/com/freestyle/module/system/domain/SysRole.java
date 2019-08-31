package com.freestyle.module.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.freestyle.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 角色实体
 * @author zhangshichang
 * @date 2019/8/30 上午9:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_role")
public class SysRole extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 8647624653658770459L;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;

}
