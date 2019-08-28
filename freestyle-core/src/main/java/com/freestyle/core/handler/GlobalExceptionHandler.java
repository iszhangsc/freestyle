package com.freestyle.core.handler;

import com.freestyle.common.exception.FreestyleException;
import com.freestyle.common.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常拦截处理
 *
 * @author zhangshichang
 * @date 19-2-28 下午4:28
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {



    /**
     * 拦截业务异常类：   一般为业务逻辑错误
     * @author zhangshichang
     * @date 19-4-9 上午10:32
     * @param request 请求体
     * @param e 业务异常类
     * @return CommonResult
     */
    @ExceptionHandler(value = FreestyleException.class)
    public ResultVO handlerServiceException(HttpServletRequest request, FreestyleException e) {
        log.error("请求接口地址:{}   业务逻辑错误信息:{}", request.getRequestURI(), e.getMsg());
        return ResultVO.fail(e.getMsg());
    }


    /**
     * 拦截内部异常类：  一般为未知错误
     * @author zhangshichang
     * @date 19-4-9 上午10:32
     * @param request 请求体
     * @param e 业务异常类
     * @return CommonResult
     */
    @ExceptionHandler(value = Exception.class)
    public ResultVO handlerServiceException(HttpServletRequest request, Exception e) {
        log.error("请求接口地址:{},失败信息:{}", request.getRequestURL().toString(), e.getMessage());
        return ResultVO.error("服务器内部错误");
    }


    /**
     * 拦截所有controller方法中所有参数加了<code>@Valid</code>注解的方法
     * @author zhangshichang
     * @date 19-3-11 下午2:35
     * @param e 参数异常类
     * @return CommonResult
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultVO handlerParamErrorException(HttpServletRequest request, Exception e) {
        MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
        BindingResult result = methodArgumentNotValidException.getBindingResult();
        String message = result.getFieldErrors().get(0).getDefaultMessage();
        log.warn("请求接口地址:{}警告,信息:{}", request.getRequestURL().toString(), message);
        return ResultVO.fail(message);
    }


    /**
     * 拦截前端用户乱输入，引起的错误
     * @author zhangshichang
     * @date 19-5-20 下午5:17
     * @return   CommonResult
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResultVO handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("name:{}, propertyName:{}, message:{}, parameter:{}, localizedMessage:{}, value:{}",
                e.getName(), e.getPropertyName(), e.getMessage(),
                e.getParameter(), e.getLocalizedMessage(),
                e.getValue()
        );
        return ResultVO.fail("参数类型不匹配");
    }

    /**
     *
     * @author zhangshichang
     * @date 19-6-19 下午5:54
     * @param e http方法不知此异常
     * @return  {@link ResultVO} 通用结果集
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResultVO handlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String warnMsg = "请求方法不支持".concat("支持的方法是：".concat(e.getMethod()));
        log.warn(warnMsg);
        return ResultVO.fail(warnMsg);
    }

    /**
     * 拦截 Http Servlet请求缺少参数异常，进行友好返回给前端
     * @author zhangshichang
     * @date 19-6-19 下午2:09
     * @param e 缺少请求参数异常类
     * @return  {@link ResultVO} 通用结果集
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResultVO handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String warnMsg = "参数名:".concat(e.getParameterName())
                .concat(",参数类型:").concat(e.getParameterType())
                .concat(",警告消息:").concat(e.getLocalizedMessage());
        log.warn(warnMsg);
        return ResultVO.fail(warnMsg);
    }

    /*=========== Shiro 异常拦截==============*/

    @ExceptionHandler(value = AccountException.class)
    public ResultVO handlerAccountException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return ResultVO.fail(HttpStatus.FORBIDDEN.value(), "不正确的凭证");
    }

    @ExceptionHandler(value = IncorrectCredentialsException.class)
    public ResultVO handlerIncorrectCredentialsException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return ResultVO.fail(HttpStatus.FORBIDDEN.value(), "不正确的凭证");
    }

    @ExceptionHandler(value = UnknownAccountException.class)
    public ResultVO handlerUnknownAccountException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return ResultVO.fail(HttpStatus.FORBIDDEN.value(), "账号不存在");
    }

    @ExceptionHandler(value = LockedAccountException.class)
    public ResultVO handlerLockedAccountException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return ResultVO.fail(HttpStatus.FORBIDDEN.value(), "账号已被锁定");
    }

    @ExceptionHandler(value = ExcessiveAttemptsException.class)
    public ResultVO handlerExcessiveAttemptsException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return ResultVO.fail(HttpStatus.FORBIDDEN.value(), exception.getLocalizedMessage());
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResultVO handlerAuthenticationException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return ResultVO.fail(HttpStatus.FORBIDDEN.value(), "身份验证失败");
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResultVO handlerUnauthorizedException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return ResultVO.fail(HttpStatus.FORBIDDEN.value(), "该账号无此权限");
    }

}
