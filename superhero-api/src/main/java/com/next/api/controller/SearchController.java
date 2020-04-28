package com.next.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.next.service.MovieService;
import com.next.service.StaffService;
import com.next.utils.NEXTJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("search")
@Api(value = "搜索页", tags = {"搜索页的相关接口"})
public class SearchController extends BasicController {
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private StaffService staffService;
	
	@ApiOperation(value = "搜索电影列表", notes = "搜索电影列表", httpMethod = "POST")
	@PostMapping("/list")
	public NEXTJSONResult list(
			@ApiParam(name = "keywords", value = "查询的名称，中文/英文", required = false)
			@RequestParam String keywords, 
			@ApiParam(name = "page", value = "查询的写一页的页数", required = false)
			@RequestParam Integer page, 
			@ApiParam(name = "pageSize", value = "每页分页展示的数量", required = false)
			@RequestParam Integer pageSize) {
		
		if (StringUtils.isBlank(keywords)) {
			keywords = "";
		}
		
		if (page == null) {
			page = 1;
		}
		
		if (pageSize == null) {
			pageSize = 10;
		}
		
		return NEXTJSONResult.ok(movieService.searchTrailer(keywords, page, pageSize));
	}
	
	@ApiOperation(value = "预告片详情", notes = "预告片详情", httpMethod = "POST")
	@PostMapping("/trailer/{trailerId}")
	public NEXTJSONResult trailer(
			@ApiParam(name = "trailerId", value = "预告片的主键id", required = true)
			@PathVariable String trailerId) {
		
		if (StringUtils.isBlank(trailerId)) {
			return NEXTJSONResult.errorMsg("");
		}
		
		return NEXTJSONResult.ok(movieService.queryTrailerInfo(trailerId));
	}
	
	@ApiOperation(value = "查询演职人员", notes = "查询演职人员", httpMethod = "POST")
	@PostMapping("/staff/{trailerId}/{role}")
	public NEXTJSONResult staff(
			@ApiParam(name = "trailerId", value = "预告片的主键id", required = true)
			@PathVariable String trailerId,
			@ApiParam(name = "role", value = "演职人员的角色，[1：导演，2：演员]", required = true)
			@PathVariable Integer role) {
		
		if (StringUtils.isBlank(trailerId)) {
			return NEXTJSONResult.errorMsg("");
		}
		
		return NEXTJSONResult.ok(staffService.queryStaffs(trailerId, role));
	}
}
