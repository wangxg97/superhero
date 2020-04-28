package com.next.api.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.next.pojo.Users;
import com.next.pojo.bo.ModifiedUserBO;
import com.next.pojo.bo.RegistLoginUsersBO;
import com.next.pojo.vo.UsersVO;
import com.next.service.UserService;
import com.next.utils.DateUtil;
import com.next.utils.MD5Utils;
import com.next.utils.NEXTJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("user")
@Api(value = "用户", tags = {"用户相关接口"})
public class UsersController extends BasicController {
	
	@Autowired
	private UserService userService;
	
	@ApiOperation(value = "用户注册登录", notes = "用户注册登录", httpMethod = "POST")
	@PostMapping("/registOrLogin")
	public NEXTJSONResult registOrLogin(
			@RequestBody RegistLoginUsersBO userBO) throws Exception {
		
		// 0. 判断用户名和密码不能为空
		if (StringUtils.isBlank(userBO.getPassword()) || StringUtils.isBlank(userBO.getUsername())) {
			return NEXTJSONResult.errorMsg("用户名或密码不能为空...");
		}
		
		Users userResult = null;
		// 1. 根据用户名判断，用户记录是否存在
		boolean usernameIsExist = userService.queryUsernameIsExist(userBO.getUsername());
		if (usernameIsExist) {
			// 1.1 用户登录
			userResult = userService.queryUserForLogin(userBO.getUsername(), 
									MD5Utils.getMD5Str(userBO.getPassword()));
		} else {
			// 1.2 用户注册
			userResult = userService.saveUser(userBO);
		}
		
		// 2. 设置用户分布式会话 并且 return 到前端
		return NEXTJSONResult.ok(setRedisUserToken(userResult));
	}
	
	
	@ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
	@PostMapping("/logout")
	public NEXTJSONResult logout(
			@ApiParam(name = "userId", value = "用户id主键", required = true)
			@RequestParam String userId) {
		redis.del(REDIS_USER_TOKEN + ":" + userId);
		return NEXTJSONResult.ok();
	}
	
	@ApiOperation(value = "用户头像上传", notes = "用户头像上传", httpMethod = "POST")
	@PostMapping(value = "/uploadFace", headers = "content-type=multipart/form-data")
	public NEXTJSONResult uploadFace(
			@ApiParam(name = "userId", value = "用户id主键", required = true)
			@RequestParam String userId,
			@ApiParam(name = "file", value = "用户头像", required = true)
			MultipartFile file) {
		
//		System.out.println(faceConfig);
		// 获取文件保存的命名空间
		String fileSpace = faceConfig.getFaceFileSpace();
		
		// 定义前缀，用于保存到数据库中，进行拼接的路径
		String uploadPathPrefix = File.separator + userId;
		
		if (file != null) {
			FileOutputStream fileOutputStream = null;
			InputStream inputStream = null;
			try {	
				// 获取文件上传时候的文件名
				String fileName = file.getOriginalFilename();
				if(StringUtils.isNotBlank(fileName)) {
					// 重新组装文件名
					String fileNameArr[] = fileName.split("\\.");
					String suffix = fileNameArr[fileNameArr.length - 1];
					String newFileName = "face-" + userId + "." + suffix;
					
					uploadPathPrefix += (File.separator + newFileName);
					
					// 组装文件最终保存的一个地址路径
					String finalFilePath = fileSpace + 
											uploadPathPrefix + 
											File.separator + 
											newFileName;
					File outFace = new File(finalFilePath);
					if (outFace.getParentFile() != null || !outFace.getParentFile().isDirectory()) {
						outFace.getParentFile().mkdirs();
					}
					
					// 拷贝文件到输出文件对象
					fileOutputStream = new FileOutputStream(outFace);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fileOutputStream != null) {
						fileOutputStream.flush();
						fileOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
		
		// 针对用户新头像，保存到数据库
		String newUserFaceUrl = faceConfig.getUrl() + uploadPathPrefix;
		// 为了保证前端每次上传拿到的图片地址都是最新的，并且是需要刷新的
		newUserFaceUrl += ("?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN));
		
		// 保存地址到数据库
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(newUserFaceUrl);
		user = userService.updateUserInfo(user);
		user.setPassword(null);
		
		return NEXTJSONResult.ok(conventUsersVO(user));
	}
	
	private UsersVO conventUsersVO(Users user) {
		String token = redis.get(REDIS_USER_TOKEN + ":" + user.getId());
		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(user, userVO);
		userVO.setUserUniqueToken(token);
		return userVO;
	}
	
	@ApiOperation(value = "修改昵称", notes = "修改昵称", httpMethod = "POST")
	@PostMapping("/modifyUserinfo")
	public NEXTJSONResult modifyUserinfo(
			@RequestBody ModifiedUserBO userBO) {
		
		String userId = userBO.getUserId();
		if (StringUtils.isBlank(userId)) {
			return NEXTJSONResult.errorMsg("用户id不能为空");
		}
		
		// 以下三个属性必须至少有一项是不为空的
		String birthday = userBO.getBirthday();
		Integer sex = userBO.getSex();
		String nickname = userBO.getNickname();
		if (StringUtils.isBlank(birthday) && StringUtils.isBlank(nickname) && sex == null) {
			return NEXTJSONResult.errorMsg("修改用户信息不能为空");
		}
		
		if (sex != null && sex != 0 && sex != 1) {
			return NEXTJSONResult.errorMsg("性别不正确 - [1:男][0:女]");
		}
		
		if (StringUtils.isNotBlank(nickname) && nickname.length() > 12) {
			return NEXTJSONResult.errorMsg("昵称长度不能超过12位");
		}
		
		if (StringUtils.isNotBlank(birthday)) {
			if (!DateUtil.isValidDate(birthday, DateUtil.ISO_EXPANDED_DATE_FORMAT)) {
				return NEXTJSONResult.errorMsg("生日日期格式不正确");
			}
		}
		
		// 保存地址到数据库
		Users user = new Users();
		user.setId(userId);
		user.setSex(sex);
		user.setNickname(nickname);
		user.setBirthday(birthday);
		user = userService.updateUserInfo(user);
		user.setPassword(null);
		
		return NEXTJSONResult.ok(conventUsersVO(user));
	}
	
	public void test(){
		System.out.println("wwww  fgggg --合并分支");
	}


}
