package cn.itcast.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.User;
import cn.itcast.service.UserService;
import cn.itcast.utils.PageBean;
import net.sf.json.JsonConfig;
@Controller("userAction")
@Scope("prototype")
public class UserAction extends ActionSupport implements ModelDriven<User> {
	private User user = new User();
	@Resource(name="userService")
	private UserService userService ;
	
	

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private Integer page;
	private Integer rows;
	public String list() throws Exception {
		//封装离线查询对象
		DetachedCriteria dc = DetachedCriteria.forClass(User.class);
		
		//1 调用Service查询分页数据(PageBean)
		PageBean pb = userService.getPageBean(dc,page,rows);
		//2 将列表数据转换为json发送给浏览器
		//total:总记录数 
		//rows:每行显示的数据
		//{total:xx,rows:[{user_id:1,user_na me:'tom'}]}
		Map map  = new HashMap();
		map.put("total", pb.getTotalCount());
		map.put("rows", pb.getList());
		//将map转换为json    
		String json = JSON.toJSONString(map);
		
		ServletActionContext.getResponse().setContentType("application/json;charset=utf-8");
		
		ServletActionContext.getResponse().getWriter().write(json);
		
		return null;
	}
	
	public String login() throws Exception {
		//1 调用Service执行登陆逻辑
		User u = userService.getUserByCodePassword(user);
		//2 将返回的User对象放入session域
		ActionContext.getContext().getSession().put("user", u);
		//3 重定向到项目首页
		return "toHome";
	}
	
	public String toEdit() throws Exception {
		//调用Service根据id查询User对象
		User u = userService.getById(user.getUser_id());
		//设置密码为空,禁止回显
		u.setUser_password("");
		//以json形式输出到浏览器
		String json = JSON.toJSONString(u);
		ServletActionContext.getResponse().setContentType("application/json;charset=utf-8");
		
		ServletActionContext.getResponse().getWriter().write(json);
		return null;
	}
	public String delete() throws Exception {
		//调用Service根据id查询User对象
		userService.deleteById(user.getUser_id());
		return null;
	}
	public String regist() throws Exception {
			//1 调用Service保存注册用户
			try {
				userService.saveUser(user);
			} catch (Exception e) {
				e.printStackTrace();
				ActionContext.getContext().put("error", e.getMessage());
				return "regist";
			}
			//2 重定向到登陆页面
		return "toLogin";
}

	@Override
	public User getModel() {
		return user;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	
	
}
