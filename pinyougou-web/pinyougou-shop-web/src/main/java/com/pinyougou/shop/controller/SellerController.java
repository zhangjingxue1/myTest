package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/seller")
public class SellerController {

    /**
     * 发现商家对象
     */
    @Reference(timeout = 10000)
    private SellerService sellerService;


    /**
     * 添加商家
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller) {
        try {
            //让你哭加密
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String password = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(password);

            sellerService.save(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 数据回显
     */
    @GetMapping("/showMyData")
    public Seller showMyData() {
        try {
            //首先获取用户登陆id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //通过此id查询用户信息
            return sellerService.findOne(sellerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改用户数据保存
     */
    @PostMapping("/upMyData")
    public boolean upMyData(@RequestBody Seller seller) {
        try {
            //首先获取用户登陆id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            seller.setSellerId(sellerId);
            sellerService.update(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改密码
     */
    @PostMapping("/upPassword")
    public String upPassword(@RequestBody Map<String, String> password) {
        try {
            //这里没做非空判断麻烦- -
            String newPassword = password.get("newPassword");
            String checkNewPassword = password.get("checkNewPassword");
            //以防禁用了浏览器JS的用户还是需要校验一下,非空判断就没做了校验太多了
            if (!newPassword.equals(checkNewPassword)) {
                return "新密码与确认密码不一致!";
            }
            //获取用户登陆id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            //通过seller查询数据库获得用户信息
            Seller seller = sellerService.findOne(sellerId);
            //让你哭加密
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //拿到原密码
            String oldPassword = password.get("oldPassword");
            //调用matches比较两个密码是否一样
            if (!passwordEncoder.matches(oldPassword, seller.getPassword())) {
                return "原密码输入不正确,请检查!";
            }
            //其他条件排除后把新密码修改到数据库
            String encodePassword = passwordEncoder.encode(newPassword);

            //把新的密码封装好提交给数据库
            Seller newSeller = new Seller();
            newSeller.setPassword(encodePassword);
            newSeller.setSellerId(sellerId);
            sellerService.update(newSeller);

            //没出现问题就不用返回错误信息;
            return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "服务器忙!操作失败";
    }
}
