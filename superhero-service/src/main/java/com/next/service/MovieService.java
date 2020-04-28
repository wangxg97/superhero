package com.next.service;

import java.util.List;

import com.next.pojo.Movie;
import com.next.utils.JqGridResult;

public interface MovieService {

	/**
	 * 查询热么电影预告片
	 * 	评分越高，超英越热门
	 * 	点赞数越高，预告片越热门
	 * @param type
	 * @return
	 */
	public List<Movie> queryHotSuperhero(String type);
	
	/**
	 * 查询电影预告表的记录数
	 * @return
	 */
	public Integer queryAllTrailerCounts();
	
	/**
	 * 查询所有的电影记录
	 * @return
	 */
	public List<Movie> queryAllTrailers();
	
	/**
	 * 根据关键字查询分页的电影内容
	 * @return
	 */
	public JqGridResult searchTrailer(String keywords, Integer page, Integer pageSize);
	
	/**
	 * 根据主键查询电影详情
	 * @return
	 */
	public Movie queryTrailerInfo(String trailerId);
}
