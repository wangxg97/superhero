//package com.next.api;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import com.next.pojo.Movie;
//import com.next.redis.RedisOperator;
//import com.next.service.MovieService;
//import com.next.utils.DateUtil;
//import com.next.utils.JsonUtils;
//
///**
// * 自启动类
// */
//@Component
//public class AutoBootRunner implements ApplicationRunner {
//
//	@Autowired
//	private MovieService movieService;
//	
//	@Autowired
//	private RedisOperator redis;
//	
//	@Override
//	public void run(ApplicationArguments args) throws Exception {
//		// 1. 获得所有的电影记录
//		List<Movie> allList = movieService.queryAllTrailers();
//		
//		// 2. 向redis存入每一条电影的记录
//		for (int i = 0 ; i < allList.size() ; i ++) {
//			Movie movie = allList.get(i);
//			redis.set("guess-trailer-id:" + i, JsonUtils.objectToJson(movie));
//			
//			/**
//			 * 用于推送业务
//			 */
//			Date releaseDate = movie.getCreateTime();
//			String releaseDateStr = DateUtil.dateToString(releaseDate, 
//									DateUtil.ISO_EXPANDED_DATE_FORMAT);
//			String releaseDateKey = "movie:" + releaseDateStr;
//			String movieListStr = redis.get(releaseDateKey);
//			List<Movie> releaseList = new ArrayList<Movie>();
//			if (StringUtils.isNotEmpty(movieListStr)) {
//				releaseList = JsonUtils.jsonToList(movieListStr, Movie.class);
//			}
//			
//			// buxfix: 解决每次启动重复添加已存在电影到redis
//			boolean isExist = false;
//			for (Movie m : releaseList) {
//				String mid = m.getId();
//				if (mid.equals(movie.getId())) {
//					isExist = true;
//					break;
//				}
//			}
//			if (!isExist) {
//				releaseList.add(movie);
//				redis.set(releaseDateKey, JsonUtils.objectToJson(releaseList));
//			}
//		}
//	}
//
//}
