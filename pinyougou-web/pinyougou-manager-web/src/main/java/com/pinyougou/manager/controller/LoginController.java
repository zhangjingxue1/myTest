package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义登录处理控制器
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    /**
     * 注入身份认证管理器
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 自定义登录认证有验证码功能
     */
    @RequestMapping("/login")
    public void login(String username, String password,
                      HttpServletRequest request, HttpServletResponse response) {
        try {
            // 判断请求方式
            if (request.getMethod().equalsIgnoreCase("post")) {
                //获取系统生成的验证码
                String checkCode = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
                //获取用户输入验证码判断
                String check = request.getParameter("check");
                //校验验证码(试试不作非空判断会不会炸)...没炸!
                /*if (checkCode != null && check != null) {

                }*/
                if (!checkCode.equalsIgnoreCase(check)) {
                    // 验证码不正确重定向到登录页面
                    request.getRequestDispatcher(request.getContextPath() + "/login.html").forward(request, response);
                }
                // 创建用户名与密码认证对象
                UsernamePasswordAuthenticationToken token
                        = new UsernamePasswordAuthenticationToken(username, password);
                // 调用认证方法，返回认证对象.如果认证不正确会有异常产生
                Authentication authenticate = authenticationManager.authenticate(token);
                // 判断是否认证成功
                if (authenticate.isAuthenticated()) {
                    // 设置用户认证成功，往Session中添加认证通过信息
                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticate);
                    //认证成功跳转到index页面
                    response.sendRedirect(request.getContextPath() + "/admin/index.html");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //认证失败请求转发到login.html页面上
        try {
            request.getRequestDispatcher(request.getContextPath() + "/login.html").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //显示登陆用户名
    @GetMapping("/showLoginName")
    public Map<String,String> showLoginName(){
        //获取登陆用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap<String, String> data = new HashMap<>();
        data.put("loginName",loginName);
        return data;
    }
}
