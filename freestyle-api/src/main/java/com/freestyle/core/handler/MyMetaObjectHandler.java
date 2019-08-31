package com.freestyle.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.freestyle.common.enums.DefaultValueField;
import com.freestyle.module.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动注入数据创建人、创建时间、修改人、修改时间
 * @author zhangshichang
 * @date 2019/8/31 下午3:28
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**    
     * 保存时插入默认值方法实现
     * @author zhangshichang  
     * @date 2019/8/31 下午3:30  
     * @param metaObject    元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insert fill start");
        final SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        // 参数说明                   字段属性名      默认值       元对象
        this.setInsertFieldValByName(DefaultValueField.createTime.name(), new Date(), metaObject);
        this.setInsertFieldValByName(DefaultValueField.updateTime.name(),new Date(), metaObject);
        this.setInsertFieldValByName(DefaultValueField.createBy.name(), sysUser.getUsername(), metaObject);
    }

    /**
     * 更新时插入默认值方法实现
     * @author zhangshichang
     * @date 2019/8/31 下午3:30
     * @param metaObject    元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("update fill start");
        final SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        // 参数说明                   字段属性名      默认值       元对象
        this.setUpdateFieldValByName(DefaultValueField.updateTime.name(), new Date(), metaObject);
        this.setUpdateFieldValByName(DefaultValueField.updateBy.name(), sysUser.getUsername(), metaObject);
    }

}
