package com.next.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.next.pojo.Movie;
import com.next.pojo.bo.MovieMQMsgBO;
import com.next.redis.RedisOperator;
import com.next.utils.DateUtil;
import com.next.utils.JsonUtils;
import com.next.utils.MoviePushUtil;

/**
 * 定时任务处理类
 */
@Component
public class MyJob {
	
	@Autowired
	private RedisOperator redis;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
//	0/5 * * * * ? 	每个5秒执行
//	0 0 1 * * ? 	每天凌晨1点
	@Scheduled(cron = "0/10 * * * * ?")
	public void test() {
//		System.out.println(DateUtil.dateToString(new Date(),
//							DateUtil.DATETIME_PATTERN));
		// 1. 从redis中获得第二天将要上映的电影名称
		Date tomorrow = DateUtil.dateIncreaseByDay(new Date(), 1);
		String tomorrowStr = DateUtil.dateToString(tomorrow, 
						DateUtil.ISO_EXPANDED_DATE_FORMAT);
		String releaseDateKey = "movie:" + tomorrowStr;
		
		String movieListStr = redis.get(releaseDateKey);
		if (StringUtils.isNotEmpty(movieListStr)) {
			List<Movie> releaseList = JsonUtils.jsonToList(movieListStr, Movie.class);
			String movieNames = "";
			if (releaseList != null && releaseList.size() > 0) {
				for (Movie m : releaseList) {
					movieNames += ( "《" + m.getName() + "》，");
				}
			}
			String title = "新片速递!";
			String text = movieNames + "将于明日上映~~~";
			
			
//			使用mq进行业务解耦
//			MoviePushUtil.doPush(title, text);
			MovieMQMsgBO msg = new MovieMQMsgBO(title, text);
			rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_PUSH,
					"push.new.movie.display", JsonUtils.objectToJson(msg));
		}
	}

	
}
