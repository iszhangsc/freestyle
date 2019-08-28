package com.freestyle.module.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 系统用户实体
 * @author zhangshichang
 * @date 2019/8/27 下午2:54
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_user")
public class SysUser {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private String username;

    private String password;

    private String mobile;
    /**
     * 性别（1：男 2：女）
     */
    private Integer sex;

    private String remark;

    private Date createTime;

    private String createBy;

    private String updateBy;

    private Date updateTime;
    /**
     * 头像
     */
    private String avatar;
}
