package com.freestyle.module.system.shiro.authc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freestyle.common.util.JwtUtil;
import com.freestyle.common.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

/**
 * 鉴权登录拦截器
 * @author zhangshichang
 * @date 2019/8/27 下午2:43
 */
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {


	/**
	 * 该方法最先执行
	 * 对跨域提供支持
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
		httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
		// 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}

	/**
	 * 执行登录认证
	 *
	 * @param request   请求体
	 * @param response  响应体
	 * @param mappedValue	为匹配请求的过滤器链中的过滤器指定的配置。
	 * @return  boolean
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		try {
			return executeLogin(request, response);
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			responseMsg(response, HttpStatus.UNAUTHORIZED.value(), e.getLocalizedMessage(),httpServletRequest.getServletPath());
		}
		return false;
	}

	/**
	 * 自定义执行登录的方法
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String token = httpServletRequest.getHeader(JwtUtil.TOKEN_NAME);
		JwtToken jwtToken = new JwtToken(token);
		// 提交给自定义 TokenRealm 进行登入，如果错误它会抛出异常并被捕获
		getSubject(request, response).login(jwtToken);
		// 如果没有抛出异常则代表登入成功，返回true
		return true;
	}

	/**
	 * 通过流的形式输出JSON信息到前端
	 * @param response 响应体
	 */
	private void responseMsg(ServletResponse response, int status, String msg, String path)  {
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.setStatus(status);
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json;charset=UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		final ResultVO<Object> result = ResultVO.fail(resp.getStatus(), msg, path);
		// 简化 try catch finally 此处关闭了writer对象的
		try (PrintWriter writer = resp.getWriter()) {
			writer.write(objectMapper.writeValueAsString(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
