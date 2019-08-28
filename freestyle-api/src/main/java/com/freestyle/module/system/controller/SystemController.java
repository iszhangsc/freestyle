package com.freestyle.module.system.controller;

import com.freestyle.common.vo.ResultVO;
import com.freestyle.module.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author zhangshichang
 * @date 2019/8/28 下午4:47
 */
@Slf4j
@RestController
@RequestMapping("/sys")
public class SystemController {

    private final SysUserService sysUserService;

    public SystemController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @RequestMapping("/403")
    public ResultVO unauthorizedUrl(){
        return ResultVO.fail(401, "无权限");
    }

    @RequestMapping("/common/notLogin")
    public void notLogin(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(ResultVO.fail(HttpServletResponse.SC_UNAUTHORIZED, "请登录").toString());
    }

    @PostMapping("/login")
    public ResultVO login(@RequestBody Map<String, String> map) {
        return ResultVO.ok();
    }


    @GetMapping("/needLogin")
    public ResultVO needLogin() {
        return ResultVO.ok("需要登录哦 ..");
    }

    @GetMapping("/needNotLogin")
    public ResultVO needNotLogin() {
        return ResultVO.ok("Ni ..");
    }

    @GetMapping("/noLogin")
    public ResultVO noLogin() {
        return ResultVO.ok("不用登录的接口");
    }

}
