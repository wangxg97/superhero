package com.next.service.impl;

import java.util.Date;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.next.mapper.UsersMapper;
import com.next.pojo.Users;
import com.next.pojo.bo.MPWXUserBO;
import com.next.pojo.bo.RegistLoginUsersBO;
import com.next.service.UserService;
import com.next.utils.MD5Utils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private Sid sid;
	
	private final static String USER_DEFAULT_FACE_IMAGE_URL = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";
	
	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserForLoginByMPWX(String openId) {
		
		Example example = new Example(Users.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("mpWxOpenId", openId);
		
		return usersMapper.selectOneByExample(example);
	}

	@Transactional (propagation = Propagation.REQUIRED)
	@Override
	public Users saveUserMPWX(String openId, MPWXUserBO wxUserBO) {
		
		String userid = sid.nextShort();
		
		Users user = new Users();
		user.setId(userid);
		
		user.setNickname(wxUserBO.getNickName());
		user.setFaceImage(wxUserBO.getAvatarUrl());
		user.setMpWxOpenId(openId);
		
		user.setBirthday("1900-01-01");
		user.setIsCertified(0);
		user.setRegistTime(new Date());
		
		usersMapper.insert(user);
		
		return user;
	}

	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExist(String username) {
		
		Users user = new Users();
		user.setUsername(username);
		Users result = usersMapper.selectOne(user);
		
		return result != null ? true : false;
	}

	@Transactional (propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserForLogin(String username, String pwd) {
		
		Users user = new Users();
		user.setUsername(username);
		user.setPassword(pwd);
		Users result = usersMapper.selectOne(user);
		
		return result;
	}

	@Transactional (propagation = Propagation.REQUIRED)
	@Override
	public Users saveUser(RegistLoginUsersBO userBO) {
		
		String userid = sid.nextShort();
		
		Users user = new Users();
		user.setId(userid);
		
		user.setUsername(userBO.getUsername());
		try {
			user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		user.setNickname(userBO.getUsername());
		user.setFaceImage(USER_DEFAULT_FACE_IMAGE_URL);
		
		user.setBirthday("1900-01-01");
		user.setIsCertified(0);
		user.setRegistTime(new Date());
		
		usersMapper.insert(user);
		
		return user;
	}

	@Transactional (propagation = Propagation.REQUIRED)
	@Override
	public Users updateUserInfo(Users user) {
		usersMapper.updateByPrimaryKeySelective(user);
		return queryUserInfoById(user.getId());
	}
	
	@Transactional (propagation = Propagation.SUPPORTS)
	private Users queryUserInfoById(String userId) {
		return usersMapper.selectByPrimaryKey(userId);
	}

}
