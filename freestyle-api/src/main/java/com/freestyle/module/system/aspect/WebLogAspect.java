package com.freestyle.module.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freestyle.common.vo.ResultVO;
import com.freestyle.core.component.SpringContextComponent;
import com.freestyle.module.system.shiro.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 日志切面
 *
 * @author zhangshichang
 * @date 19-3-22 下午9:35
 */
@Slf4j
@Aspect
@Component
@Order(value = 1)
public class WebLogAspect {

    /**
     * com.freestyle.module.*.controller 包下定义的所有请求为切入点
     *
     * @author zhangshichang
     * @date 19-3-22 下午9:37
     */
    @Pointcut("execution(public * com.freestyle.module.*.controller..*.*(..))")
    public void webLog() {
    }


    /**
     * 在切点之前织入
     *
     * @param joinPoint 切入点
     * @author zhangshichang
     * @date 19-3-22 下午9:39
     */
    @Before("webLog()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        //开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        //打印请求相关参数
        log.info("========================================== Request Start ==========================================");
        if (request != null) {
            //打印 Http URL
            log.info("URL            : {}", request.getRequestURL().toString());
            //打印 Http method
            log.info("HTTP Method    : {}", request.getMethod());
            //打印调用controller的全路径及执行方法
            log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            //打印客户端真实的IP
            log.info("Client IP      : {}", IpUtils.getIpAddrByRequest(request));
            // 打印请求参数--将参数转化成JSON打印; 目前已知以下三个参数不可打印，否则序列化报错
            Object[] args = joinPoint.getArgs();
            Object[] arguments = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ServletRequest
                        || args[i] instanceof ServletResponse
                        || args[i] instanceof MultipartFile) {
                    continue;
                }
                arguments[i] = args[i];
            }
            log.info("Request param  : {}", new ObjectMapper().writeValueAsString(arguments));
        }
    }

    /**
     * 在切点之后织入
     *
     * @author zhangshichang
     * @date 19-3-22 下午10:35
     */
    @After("webLog()")
    public void doAfter() {
        log.info("=========================================== Request End ===========================================");
        //每个请求之间空一行
        log.info("");
    }

    /**
     * 环绕通知--记录后台执行时间以及将响应结果打印
     *
     * @param proceedingJoinPoint 切入点
     * @return object
     * @author zhangshichang
     * @date 19-3-22 下午10:38
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        //放行
        Object result = proceedingJoinPoint.proceed();
        final HttpServletRequest httpServletRequest = SpringContextComponent.getHttpServletRequest();
        if (result instanceof ResultVO) {
            ((ResultVO) result).setPath(httpServletRequest.getServletPath());
        }
        //打印出参
        log.info("Response param : {}", new ObjectMapper().writeValueAsString(result));
        //执行耗时
        log.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }
}