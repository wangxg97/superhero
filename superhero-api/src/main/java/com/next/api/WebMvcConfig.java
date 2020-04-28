package com.next.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.next.api.controller.interceptor.UserTokenInterceper;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		/**
		 * macos: file:/temp/
		 * win:	  file:C:/temp/
		 */
		registry.addResourceHandler("/**")
				.addResourceLocations("file:/temp/")	// 映射tomcat虚拟目录
				.addResourceLocations("classpath:/META-INF/resources/");
	}
	
	@Bean
	public UserTokenInterceper userTokenInterceper() {
		return new UserTokenInterceper();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册拦截器
		registry.addInterceptor(userTokenInterceper())
				.addPathPatterns("/user/modifyUserinfo")
				.addPathPatterns("/user/uploadFace");
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}

}
