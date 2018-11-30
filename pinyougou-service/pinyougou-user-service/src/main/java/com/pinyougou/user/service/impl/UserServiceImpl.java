package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;

    /**
     * 发送验证码
     */
    @Override
    public boolean sendCode(String phone) {
        try {
            /** 生成6位随机数 */
            String code = UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .replaceAll("[a-z|A-Z]", "")
                    .substring(0, 6);
            System.out.println("验证码：" + code);
            /** 调用短信发送接口 */
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            // 创建Map集合封装请求参数.老师的这里的变量是number,我的模版定义的变量是code
            Map<String, String> param = new HashMap<>();
            param.put("phone", phone);
            param.put("signName", signName);
            param.put("templateCode", templateCode);
            //老师的
            param.put("templateParam", "{\"number\":\"" + code + "\"}");
            //我的验证码模版(注意此处切换模版user的service层配置文件注意修改模版号)
//            param.put("templateParam", "{\"code\":\"" + code + "\"}");
            //我的收货模版
//            param.put("templateParam","{\"consignee\":\"死肥宅\",\"number\":\""+phone+"\"}");
            // 发送Post请求,他返回的是json字符串
            String content = httpClientUtils.sendPost(smsUrl, param);
            // 把json字符串转化成Map
            Map<String, Object> resMap = JSON.parseObject(content, Map.class);

            /** 存入Redis中(90秒) *///为啥要存入redis中,存到session中不好吗?
            // 解答session是全局的,如果定时失效了用户又要重新登陆了这样就有问题了!
            //并且以后做集群第一次用户访问session存在了一台tomcat第二次在另外一台那么就有问题了
            redisTemplate.boundValueOps(phone).set(code, 60, TimeUnit.SECONDS);
            return (boolean) resMap.get("success");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 校验短信验证码
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        try {
            //从redis中获取存储的验证码
            String sysCode = redisTemplate.boundValueOps(phone).get();
            //这里拿不到会报空指针异常
            return StringUtils.isNoneBlank(smsCode) && sysCode.equals(smsCode);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public User findUserByName(String username) {
        try {
            //调用数据访问层查询用户
            return userMapper.findUserByName(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 密码设置保存
     */
    @Override
    public void setSave(String userId, String username, String newPassword) {
        try {
            //根据原本的用户名查询用户
            User user = findUserByName(userId);
            //修改用户名
            user.setUsername(username);
            //修改更新日期
            user.setUpdated(new Date());
            //使用md5加密
            String password = DigestUtils.md5Hex(newPassword);
            user.setPassword(password);
            //按条件更新
            userMapper.updateByPrimaryKeySelective(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重新绑定电话
     */
    @Override
    public void updatePhone(String username, String newPhone) {
        try {
            userMapper.updatePhone(username, newPhone);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户注册
     */
    @Override
    public void save(User user) {
        try {
            // 创建日期
            user.setCreated(new Date());
            //修改日期
            user.setUpdated(user.getCreated());
            //使用md5加密
            String password = DigestUtils.md5Hex(user.getPassword());
            user.setPassword(password);

            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户信息修改
     */
    @Override
    public void update(User user) {
        try {
            userMapper.updateByPrimaryKeySelective(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

}
