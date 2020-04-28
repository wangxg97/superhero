package com.next.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.next.mapper.MovieMapper;
import com.next.pojo.Movie;
import com.next.service.MovieService;
import com.next.utils.JqGridResult;
import com.next.utils.MovieTypeEnum;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieMapper movieMapper;
	
	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public List<Movie> queryHotSuperhero(String type) {
		
		// 定义分页，永远只拿第一页的数据，分别是第一页的前8/前4条记录
		Integer page = 1;
		Integer pageSize = 0;
		
		Example example = new Example(Movie.class);
		if (type.equals(MovieTypeEnum.SUPERHERO.type)) {
			example.orderBy("score").desc();
			pageSize = 8;
		} else if (type.equals(MovieTypeEnum.TRAILER.type)) {
			example.orderBy("prisedCounts").desc();
			pageSize = 4;
		} else {
			return null;
		}
		
		PageHelper.startPage(page, pageSize);
		
		return movieMapper.selectByExample(example);
	}

	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public Integer queryAllTrailerCounts() {
		return movieMapper.selectCount(new Movie());
	}

	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public List<Movie> queryAllTrailers() {
		return movieMapper.selectAll();
	}

	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public JqGridResult searchTrailer(String keywords, Integer page, Integer pageSize) {
		
		Example example = new Example(Movie.class);
		example.orderBy("createTime").desc();
		
		Criteria criteria = example.createCriteria();
		criteria.orLike("name", "%" + keywords + "%");
		criteria.orLike("originalName", "%" + keywords + "%");
		
		PageHelper.startPage(page, pageSize);
		
		List<Movie> list = movieMapper.selectByExample(example);
		
		// 获得分页后的分页数据信息
		PageInfo<Movie> pagedList = new PageInfo<>(list);
		
		JqGridResult grid = new JqGridResult();
		grid.setPage(page);
		grid.setRows(list);
		grid.setTotal(pagedList.getPages());
		grid.setRecords(pagedList.getTotal());
		
		return grid;
	}

	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public Movie queryTrailerInfo(String trailerId) {
		return movieMapper.selectByPrimaryKey(trailerId);
//		Movie m = new Movie();
//		m.setId(trailerId);
//		movieMapper.selectOne(m);
	}

}
