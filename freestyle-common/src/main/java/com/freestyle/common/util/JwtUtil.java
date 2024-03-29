package com.freestyle.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.freestyle.common.exception.FreestyleException;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * JWT 工具类
 * @author zhangshichang
 * @date 2019/8/28 上午11:42
 */

public class JwtUtil {

	/**
	 *
	 过期时间30分钟
 	 */
	public static final long EXPIRE_TIME = 30 * 60 * 1000;

	/**
	 * token名称
	 */
	public static final String TOKEN_NAME = "X-Access-Token";

	/**
	 * 校验token是否正确
	 *
	 * @param token  密钥
	 * @param secret 用户的密码
	 * @return 是否正确
	 */
	public static boolean verify(String token, String username, String secret) {
		try {
			// 根据密码生成JWT效验器
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).build();
			// 效验TOKEN
			DecodedJWT jwt = verifier.verify(token);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	/**
	 * 获得token中的信息无需secret解密也能获得
	 *
	 * @return token中包含的用户名
	 */
	public static String getUsername(String token) {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getClaim("username").asString();
		} catch (JWTDecodeException e) {
			return null;
		}
	}

	/**
	 * 生成签名,5min后过期
	 *
	 * @param username 用户名
	 * @param secret   用户的密码
	 * @return 加密的token
	 */
	public static String sign(String username, String secret) {
		Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
		Algorithm algorithm = Algorithm.HMAC256(secret);
		// 附带username信息
		return JWT.create().withClaim("username", username).withExpiresAt(date).sign(algorithm);

	}

	/**
	 * 根据request中的token获取用户账号
	 * 
	 * @param token token
	 * @return  用户名
	 * @throws FreestyleException 自定义异常
	 */
	public static String getUserNameByToken(String token) {
		// String accessToken = request.getHeader("X-Access-Token");
		String username = getUsername(token);
		if (StringUtils.isBlank(username)) {
			throw new FreestyleException(400, "未获取到用户");
		}
		return username;
	}

}
