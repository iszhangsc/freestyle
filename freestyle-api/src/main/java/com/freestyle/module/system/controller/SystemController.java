package com.freestyle.module.system.controller;

import com.freestyle.common.constant.CacheConstant;
import com.freestyle.common.util.JwtUtil;
import com.freestyle.common.vo.ResultVO;
import com.freestyle.core.component.RedisComponent;
import com.freestyle.module.system.domain.SysUser;
import com.freestyle.module.system.service.SysUserService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zhangshichang
 * @date 2019/8/28 下午4:47
 */
@Slf4j
@RestController
@RequestMapping("/sys")
public class SystemController {

    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private SysUserService sysUserService;


    @RequestMapping("/403")
    public ResultVO unauthorizedUrl(){
        return ResultVO.fail("无权限");
    }

    @GetMapping("/needPermission")
    @RequiresPermissions("user:add")
    public ResultVO needPermission() {
        return ResultVO.ok("user:add");
    }

    @GetMapping("/needNotLogin")
    public ResultVO needNotLogin() {
        return ResultVO.ok("");
    }

    @RequiresPermissions("performance")
    @RequestMapping("/needLogin")
    public ResultVO notLogin() {
        return ResultVO.ok("您拥有性能监控权限");
    }

    @PostMapping("/login")
    public ResultVO login(@RequestBody Map<String, String> map) {
        final String username = map.get("username");
        final String password = map.get("password");
        final SysUser user = sysUserService.getUserByName(username);
        if (user == null) {
            return ResultVO.fail("用户名或密码错误");
        }
        if (!password.equals(user.getPassword())) {
            return ResultVO.fail("用户名或密码错误");
        }
        return generatedToken(user);
    }

    /**
     * 生成token,并放入redis
     * @param sysUser   用户信息
     * @return  ResultVO
     */
    private ResultVO generatedToken(SysUser sysUser) {
        final String username = sysUser.getUsername();
        final String password = sysUser.getPassword();
        final String token = JwtUtil.sign(username, password);
        redisComponent.set(CacheConstant.USER_TOKEN_CACHE_PREFIX.concat(token), token, JwtUtil.EXPIRE_TIME);
        Map<String, Object> result = Maps.newHashMap();
        result.put("username", username);
        result.put(JwtUtil.TOKEN_NAME, token);
        return ResultVO.ok(result);
    }

}
