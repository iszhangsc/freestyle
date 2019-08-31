package com.freestyle.module.system.controller;

import com.freestyle.common.vo.ResultVO;
import com.freestyle.module.system.domain.SysUser;
import com.freestyle.module.system.service.SysUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户路由
 * @author zhangshichang
 * @date 2019/8/31 下午3:58
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final SysUserService sysUserService;

    public UserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @PostMapping("")
    public ResultVO save(@RequestBody SysUser sysUser) {
        final boolean flag = sysUserService.save(sysUser);
        return flag ? ResultVO.ok() : ResultVO.fail("失败");
    }

    @PutMapping("")
    public ResultVO edit(@RequestBody SysUser sysUser) {
        final boolean flag = sysUserService.updateById(sysUser);
        return flag ? ResultVO.ok() : ResultVO.fail("失败");
    }
}
