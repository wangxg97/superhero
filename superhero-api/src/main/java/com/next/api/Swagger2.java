package com.next.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger2的配置类，为文档restful api服务
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
	
	// http://[ip]:[port]/[prjName]/swagger-ui.html
	// http://[ip]:[port]/[prjName]/doc.html

	/**
	 * swagger2 的配置项，可以配置swagger2的一些基本内容以及扫描的api
	 * @return
	 */
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.next.api.controller"))
				.paths(PathSelectors.any())
				.build();
	}
	
	/**
	 * 构建文档api的基本信息
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("电影预告api接口文档")		// 设置标题
				.contact(new Contact("next学院", "www.qq.com", "next@next.com"))
				.description("电影预告文档的描述信息")
				.version("1.0.1")
				.termsOfServiceUrl("www.qq.com")
				.build();
	}
	
}
