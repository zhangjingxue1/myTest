package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
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
