package com.pinyougou.user.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 该服务类,不作登陆控制,只用来获取角色权限数据,登陆由CAS.
 * 用户认证服务类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        //这里不作登陆控制,只做权限控制,登陆交给CAS,但是这里也能获取到登陆用户名
        //创建List集合封装角色
        List<GrantedAuthority> authorities = new ArrayList<>();
        //添加角色
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username, "", authorities);
    }

}
