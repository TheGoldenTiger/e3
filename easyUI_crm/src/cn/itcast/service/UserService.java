package cn.itcast.service;

import org.hibernate.criterion.DetachedCriteria;

import cn.itcast.domain.User;
import cn.itcast.utils.PageBean;

public interface UserService {
	//登陆方法
	User	getUserByCodePassword(User u);
	//注册用户
	void saveUser(User u);
	//查询用户列表
	PageBean getPageBean(DetachedCriteria dc, Integer currentPage, Integer pageSize);
	//根据id获得用户
	User getById(Long user_id);
	//根据id删除用户
	void deleteById(Long user_id);
}
