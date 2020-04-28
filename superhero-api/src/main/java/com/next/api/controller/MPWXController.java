package com.next.api.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.next.pojo.Users;
import com.next.pojo.bo.MPWXUserBO;
import com.next.pojo.bo.WXSessionBO;
import com.next.pojo.vo.UsersVO;
import com.next.service.UserService;
import com.next.utils.HttpClientUtil;
import com.next.utils.JsonUtils;
import com.next.utils.MPWXConfig;
import com.next.utils.NEXTJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
//@RequestMapping("stu")
@Api(value = "微信小程序授权登录", tags = {"微信小程序授权登录"})
public class MPWXController extends BasicController {
	
	@Autowired
	private UserService userService;
	
	@ApiOperation(value = "微信登录", notes = "微信登录", httpMethod = "POST")
	@PostMapping("/mpWXLogin/{code}")
	public NEXTJSONResult wxLogin(
			@ApiParam(name = "code", value = "jscode, 授权凭证", required = true)
			@PathVariable String code,
			@RequestBody MPWXUserBO wxUserBO) {
		
		String appId = MPWXConfig.APPID;
		String secret = MPWXConfig.SECRET;
		
//		https://api.weixin.qq.com/sns/jscode2session?
//			appid=APPID&
//			secret=SECRET&
//			js_code=JSCODE&
//			grant_type=authorization_code
		
		String url = "https://api.weixin.qq.com/sns/jscode2session";
		Map<String, String> param = new HashMap<>();
		param.put("appid", appId);
		param.put("secret", secret);
		param.put("js_code", code);
		param.put("grant_type", "authorization_code");
		
		String wxResult = HttpClientUtil.doGet(url, param);
		System.out.println(wxResult);
		
		WXSessionBO model = JsonUtils.jsonToPojo(wxResult, WXSessionBO.class);
		String openId = model.getOpenid();
		
		// 根据openId查询用户是否存在，存在则登录，不存在则注册
		Users user = userService.queryUserForLoginByMPWX(openId);
		if (user == null) {
			user = userService.saveUserMPWX(openId, wxUserBO);
		}
		
		// 保存用户的分布式会话 - 生成一个token保存到redis中，可以被任何分布式集群节点访问到
		String userId = user.getId();
		// 生成一个token
		String uniqueToken = UUID.randomUUID().toString().trim();
		// 保存用户会话
		redis.set(REDIS_USER_TOKEN + ":" + userId, uniqueToken);
		
		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(user, userVO);
		userVO.setUserUniqueToken(uniqueToken);
		
		return NEXTJSONResult.ok(userVO);
	}
}
