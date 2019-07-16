package cn.e3mall.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.LoginService;
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	@Override
	public E3Result login(String username, String password) {
		// 1、判断用户名密码是否正确。
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		//查询用户信息
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return E3Result.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		//校验密码
		if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
			return E3Result.build(400, "用户名或密码错误");
		}
		// 2、登录成功后生成token。Token相当于原来的jsessionid，字符串，可以使用uuid。
		String token = UUID.randomUUID().toString();
		// 3、把用户信息保存到redis。Key就是token，value就是TbUser对象转换成json。
		// 4、使用String类型保存Session信息。可以使用“前缀:token”为key
		user.setPassword(null);
		jedisClient.set("SESSION:" + token, JsonUtils.objectToJson(user));
		// 5、设置key的过期时间。模拟Session的过期时间。一般半个小时。
		jedisClient.expire("SESSION:"+ token, SESSION_EXPIRE);
		// 6、返回E3Result包装token。
		return E3Result.ok(token);
	}

}
