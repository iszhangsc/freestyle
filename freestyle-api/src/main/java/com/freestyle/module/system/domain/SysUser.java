package com.freestyle.module.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.freestyle.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 系统用户实体
 * @author zhangshichang
 * @date 2019/8/27 下午2:54
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_user")
public class SysUser extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 2419405270090751886L;

    private String username;

    private String password;

    private String mobile;
    /**
     * 性别（1：男 2：女）
     */
    private Integer sex;

    private String remark;

    /**
     * 头像
     */
    private String avatar;
}
