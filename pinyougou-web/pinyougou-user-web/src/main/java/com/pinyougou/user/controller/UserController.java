package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.ResultInfo;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference(timeout = 10000)
    private UserService userService;
    //单例设置模式,但不是一个对象哦,因为request生命周期短.这个对象就结束了下次请求又是一个新的对象
    //每次请求对象都不一样的,线程安全的
    //如果是其他的属性就是一个对象,线程不安全的.
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    //用户注册
    @PostMapping("/register")
    public boolean register(@RequestBody User user, String smsCode) {
        try {
            //先校验短信验证码是否正确
            boolean ojbk = userService.checkSmsCode(user.getPhone(), smsCode);
            if (ojbk) {
                userService.save(user);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //发送短信验证码
    @GetMapping("/sendCode")
    public boolean sendCode(String phone) {
        //String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        try {
            if (StringUtils.isNoneBlank(phone)) {
                //发送验证码
                userService.sendCode(phone);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 异步校验用户名
     */
    @GetMapping("/checkUserName")
    public ResultInfo checkUserName(String username) {
        ResultInfo resultInfo = null;
        try {
            User user = userService.findUserByName(username);
            if (user == null) {
                resultInfo = new ResultInfo(true, true, null);
            } else {
                int roll = new Random().nextInt(1000) + 1;
                resultInfo = new ResultInfo(true, false, "该用户名已经使用,建议使用:" + username + roll);
            }
            return resultInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultInfo(false, null, null);
    }

    /**
     * 密码设置
     */
    @PostMapping("/setSave")
    public ResultInfo setSave(String username, String newPassword,
                              String checkPassword) {
        ResultInfo resultInfo = null;
        try {
            if (!newPassword.equals(checkPassword)) {
                resultInfo = new ResultInfo(true, false, "确认密码与密码不一致!");
            }
            String userId = request.getRemoteUser();
            //调用服务层保存数据
            userService.setSave(userId, username, newPassword);
            resultInfo = new ResultInfo(true, true, null);
            return resultInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultInfo(false, null, null);
    }


    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/getUser")
    public User getUser() {
        try {
            String username = request.getRemoteUser();
            return userService.findUserByName(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步请求验证码跟短信验证码校验
     */
    @PostMapping("/checkCode")
    public ResultInfo checkCode(String code, String msgcode, String phone) {
        ResultInfo resultInfo = null;
        try {
            //获取系统生成的验证码
            String sysCode = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
            //获取用户输入验证码判断
            if (!sysCode.equalsIgnoreCase(code)) {
                return new ResultInfo(true, false, "验证码错误!");
            }
            //校验短信验证码是否正确
            boolean ojbk = userService.checkSmsCode(phone, msgcode);
            if (!ojbk) {
                return new ResultInfo(true, false, "短信验证码错误!");
            }
            return new ResultInfo(true, true, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultInfo(false, null, "服务器忙!稍后再试!");
    }

    /**
     * 异步请求验证码跟短信验证码校验
     */
    @PostMapping("/upPhone")
    public ResultInfo upPhone(String code, String msgcode, String newPhone) {
        ResultInfo resultInfo = null;
        try {
            //获取系统生成的验证码
            String sysCode = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
            //获取用户输入验证码判断
            if (!sysCode.equalsIgnoreCase(code)) {
                return new ResultInfo(true, false, "验证码错误!");
            }
            //校验短信验证码是否正确
            boolean ojbk = userService.checkSmsCode(newPhone, msgcode);
            if (!ojbk) {
                return new ResultInfo(true, false, "短信验证码错误!");
            }
            //获取用户名
            String username = request.getRemoteUser();
            //根据用户修改电话号码
            userService.updatePhone(username, newPhone);
            return new ResultInfo(true, true, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultInfo(false, null, "服务器忙!稍后再试!");
    }

    /**
     * 添加用户详情
     */
    @PostMapping("/infoSave")
    public void infoSave() {
        PrintWriter printWriter = null;
        try {
            //获取用户名
            String username = request.getRemoteUser();
            //通过用户名查询用户做修改准备
            User user = userService.findUserByName(username);
            //获取请求表单参数
            String nickName = request.getParameter("nickName");
            String sex = request.getParameter("sex");
            String birthday = request.getParameter("birthday");
            String provinceId = request.getParameter("provinceId");
            String cityId = request.getParameter("cityId");
            String areaId = request.getParameter("areaId");
            String job = request.getParameter("job");

            //Post请求转码
            if (nickName != null && StringUtils.isNoneBlank(nickName)) {
                user.setNickName(new String(nickName.getBytes("ISO8859-1"), "UTF-8"));
            }
            if (job != null && StringUtils.isNoneBlank(job)) {
                user.setJob(new String(job.getBytes("ISO8859-1"), "UTF-8"));
            }

            //封装生日日期
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            user.setBirthday(simpleDateFormat.parse(birthday));

            /**封装城市参数*/
            String pid = provinceId.replace("string:", "");
            String cid = cityId.replace("string:", "");
            String tid = areaId.replace("string:", "");
            String address = "{\"province\":\"" + pid + "\"," + "\"city\":\"" + cid + "\"," + "\"area\":\"" + tid + "\"}";
            user.setAddress(address);
            //更新修改日期
            user.setUpdated(new Date());
            user.setSex(sex);
            userService.update(user);
            //认证成功跳转到index页面
            response.sendRedirect("/home-setting-info.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
