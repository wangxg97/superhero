package com.next.api.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.next.api.RabbitMQConfig;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class HelloController extends BasicController {
	
	@GetMapping("/hello")
	public Object hello() {
		
		
		/**
		 * routing key 路由规则
		 * push.*.*: * 代表一个占位符
		 * 		例： push.orders.created
		 * 			push.orders.done
		 * 
		 * push.#:	# 代表多个占位符
		 * 		例： push.orders.created
		 * 			push.orders.done
		 * 			push.orders
		 * 			push.abc
		 * 			push.aaa
		 * 
		 * push.*.*.do
		 * 		例： push.orders.created.do
		 * 			push.orders.do (不会被路由规则匹配)
		 * 
		 * push.#.do:
		 * 		例： push.orders.created.do
		 * 			push.orders.do (会被路由规则匹配)
		 */
		
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_PUSH,
				"push.orders.done.display", "订单完成了 - " + new Date());
	
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_PUSH,
				"push.orders", "处理订单业务 - 未匹配");
		
		return "hello world~~";
	}
	
	
	
	
//	rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_PUSH,
//	"push.movie.on", "abc");
//rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_PUSH,
//	"push.orders.created", "订单创建了");
	
	@GetMapping("/redis/set")
	public Object set() {
		redis.set("redis-in-springboot", "hello~~~ redis~~~");
		return "设置成功";
	}
	
	@GetMapping("/redis/get")
	public Object get() {
		return redis.get("redis-in-springboot");
	}
	
}
