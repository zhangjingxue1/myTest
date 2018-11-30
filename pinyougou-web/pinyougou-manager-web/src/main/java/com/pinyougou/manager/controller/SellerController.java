package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference(timeout = 10000)
    private SellerService sellerService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Seller seller, Integer page, Integer rows) {
        try {

            if (seller != null && !StringUtils.isEmpty(seller.getName())) {
                seller.setName(new String(seller.getName().
                        getBytes("ISO8859-1"), "UTF-8"));
            }

            if (seller != null && !StringUtils.isEmpty(seller.getNickName())) {
                seller.setNickName(new String(seller.getNickName().
                        getBytes("ISO8859-1"), "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sellerService.findByPage(seller, page, rows);
    }
    /**审核通过*/
    @GetMapping("/checkPass")
    public boolean checkPass(String name,Integer status) {
        try {
            Seller seller = new Seller();
            seller.setName(name);
            if (!StringUtils.isEmpty(seller.getName())) {
                seller.setName(new String(seller.getName().
                        getBytes("ISO8859-1"), "UTF-8"));
            }
            sellerService.checkPass(seller,status);
           return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

