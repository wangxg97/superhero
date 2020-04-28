package com.next.api.controller;

import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.next.api.config.FaceConfig;
import com.next.pojo.Users;
import com.next.pojo.vo.UsersVO;
import com.next.redis.RedisOperator;

@RestController
public class BasicController {
	
	@Autowired
	public RedisOperator redis;
	
	@Autowired
	public FaceConfig faceConfig;
	
	@Autowired
	public RabbitTemplate rabbitTemplate;
	
	public static final String REDIS_USER_TOKEN = "redis-user-token";
	

	public Integer[] getGuessULikeArray(Integer counts) {
		
		Integer[] guessIndexArray = new Integer[5];
	
		for (int i = 0 ; i < guessIndexArray.length ; i ++) {
			int numIndex = (int)(Math.random() * counts);
			
			if ( ArrayUtils.contains(guessIndexArray, numIndex)) {
				i --;
				continue;
			}
			guessIndexArray[i] = numIndex;	
		}
		
		return guessIndexArray;
	}
	
	public UsersVO setRedisUserToken(Users user) {
		// 保存用户的分布式会话 - 生成一个token保存到redis中，可以被任何分布式集群节点访问到
		String userId = user.getId();
		// 生成一个token
		String uniqueToken = UUID.randomUUID().toString().trim();
		// 保存用户会话
		redis.set(REDIS_USER_TOKEN + ":" + userId, uniqueToken);
		
		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(user, userVO);
		userVO.setUserUniqueToken(uniqueToken);
		
		return userVO;
	}
}
