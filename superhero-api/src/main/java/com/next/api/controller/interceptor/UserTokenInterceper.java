package com.next.api.controller.interceptor;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.next.api.config.FaceConfig;
import com.next.redis.RedisOperator;
import com.next.utils.JsonUtils;
import com.next.utils.NEXTJSONResult;

public class UserTokenInterceper implements HandlerInterceptor {

	@Autowired
	public RedisOperator redis;
	
	public static final String REDIS_USER_TOKEN = "redis-user-token";
	
	/**
	 * 拦截请求，在controller调用之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String userId = request.getHeader("headerUserId");
		String userToken = request.getHeader("headerUserToken");
		
		if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
			String redisUserToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
			if (StringUtils.isBlank(redisUserToken)) {
//				System.out.println("用户会话过期，请重新登录...");
				returnErrorResponse(response, new NEXTJSONResult().errorTokenMsg("用户会话过期，请重新登录..."));
				return false;
			} else {
				if (!redisUserToken.equals(userToken)) {
//					System.out.println("用户在异地登录...");
					returnErrorResponse(response, new NEXTJSONResult().errorTokenMsg("用户在异地登录..."));
					return false;
				}
			}
		} else {
//			System.out.println("请登录...");
			returnErrorResponse(response, new NEXTJSONResult().errorTokenMsg("请登录..."));
			return false;
		}
		
		/**
		 * 返回 false：请求被拦截
		 * 返回 true：请求放行
		 */
		return true;
	}
	
	/**
	 * 构建一个返回json的方法
	 */
	public void returnErrorResponse(HttpServletResponse response, 
			NEXTJSONResult result) throws Exception {
		
		OutputStream out = null;
		
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/json");
			out = response.getOutputStream();
			out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
	}

	/**
	 * controller请求完毕以后，视图渲染之前
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 * controller请求完毕以后，视图渲染之后
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
