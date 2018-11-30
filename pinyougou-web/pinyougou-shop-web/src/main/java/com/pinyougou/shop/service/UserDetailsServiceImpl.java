package com.pinyougou.shop.service;

import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户认证服务类
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    /*
    这里用不了@Reference,因为开启服务器,先执行的web.xml,
    如果里面有两个配置文件,springMVC跟SpringSecurity,
    优先级是会先去监听器读取SpringSecurity配置文件,
    里面有个bean引入UserDetailsServiceImpl初始化的
    当初始化引入sellerService对象的时候,但是还没加载到springMVC,
    没到服务层发现到服务无法引入,调用该对象的方法所以会空指针异常.不能用该注解

    解决方法不能使用该注解.在SpringSecurity配置dubbo服务发现服务,
    在创建UserDetailsServiceImpl引入服务sellerService对象,
    因为这里提供set方法在引入时候因为配置了dubbo的发现服务所以能正确引入该对象
    */

    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //创建List集合封装角色
        List<GrantedAuthority> authorities = new ArrayList<>();
        //添加角色
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //根据登录名查询商家
        Seller seller = sellerService.findOne(username);
        //判断商家是否为空,并且商家已审核
        if (seller != null && "1".equals(seller.getStatus())) {
            //返回用户信息对象
            return new User(username,seller.getPassword(), authorities);
        }
        return null;
    }

    public void setSellerService(SellerService sellerService){
        this.sellerService=sellerService;
    }
}
