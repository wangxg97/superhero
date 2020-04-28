package com.next.service;

import java.util.List;

import com.next.pojo.Users;
import com.next.pojo.bo.MPWXUserBO;
import com.next.pojo.bo.RegistLoginUsersBO;
import com.next.pojo.vo.StaffVO;

public interface UserService {

	/**
	 * 根据openid查询用户是否注册过
	 * @param openId
	 * @return
	 */
	public Users queryUserForLoginByMPWX(String openId);
	
	/**
	 * 用户注册
	 * @param openId
	 * @param wxUserBO
	 * @return
	 */
	public Users saveUserMPWX(String openId, MPWXUserBO wxUserBO);
	
	/**
	 * 查询用户名是否存在
	 * @param username
	 * @return
	 */
	public boolean queryUsernameIsExist(String username);
	
	/**
	 * 用户登录
	 * @param username
	 * @param pwd
	 * @return
	 */
	public Users queryUserForLogin(String username, String pwd);
	
	/**
	 * 用户注册
	 * @param userBO
	 * @return
	 */
	public Users saveUser(RegistLoginUsersBO userBO);
	
	/**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	public Users updateUserInfo(Users user);
}
