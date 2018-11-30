package com.pinyougou.service;

import com.pinyougou.pojo.User;

import java.io.Serializable;
import java.util.List;
/**
 * UserService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface UserService{

	/** 添加方法 */
	void save(User user);

	/** 修改方法 */
	void update(User user);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	User findOne(Serializable id);

	/** 查询全部 */
	List<User> findAll();

	/** 多条件分页查询 */
	List<User> findByPage(User user, int page, int rows);
	/**发送验证码*/
    boolean sendCode(String phone);
	/**检查短信验证码是否正确*/
    boolean checkSmsCode(String phone, String smsCode);
	/**根据用户名查询用户*/
    User findUserByName(String username);
	/**密码设置保存*/
	void setSave(String userId, String username, String newPassword);
	/**根据用户名修改电话*/
	void updatePhone(String username, String newPhone);
}