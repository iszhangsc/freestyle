package com.freestyle.module.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.freestyle.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 菜单权限表
 * @author zhangshichang
 * @date 2019/8/30 上午10:26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_permission")
public class SysPermission extends BaseDomain implements Serializable {

    private static final long serialVersionUID = -8565284150203929718L;

    private Integer pId;

    private String name;

    private String permissionValue;

    private String path;

    private String icon;

    private String permissionStatus;

    private Integer flag;

}
