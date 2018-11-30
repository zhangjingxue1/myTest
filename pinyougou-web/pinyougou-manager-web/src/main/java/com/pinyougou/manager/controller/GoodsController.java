package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        try {
            if (goods != null && StringUtils.isNoneBlank(goods.getGoodsName())) {
                goods.setGoodsName(new String(goods.getGoodsName().
                        getBytes("ISO8859-1"), "UTF-8"));
            }
            return goodsService.findByPage(goods, page, rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 审核方法
     */
    @GetMapping("/check")
    public boolean check(Long[] ids, String auditStatus) {
        try {
            goodsService.upStatusById(ids, auditStatus);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除方法
     */
    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            goodsService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
