package com.next.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.next.mapper.CarouselMapper;
import com.next.pojo.Carousel;
import com.next.service.CarouselService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class CarouselServiceImpl implements CarouselService {

	@Autowired
	private CarouselMapper carouselMapper;

	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public List<Carousel> queryAll() {
		
		Example example = new Example(Carousel.class);
		example.orderBy("sort").desc();
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("isShow", 1);
		List<Carousel> carouselList = carouselMapper.selectByExample(example);
		
		return carouselList;
	}
	
}
