package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillGoodsController {
    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;

    //查询秒杀商品列表
    @GetMapping("/findSecKillGoods")
    public List<SeckillGoods> findSecKillGoods() {
        return seckillGoodsService.findSecKillGoods();
    }

    //查询秒杀商品详情
    @GetMapping("/getSecKillGood")
    public SeckillGoods getSecKillGood(Long goodId) {
        return seckillGoodsService.getSecKillGood(goodId);
    }

    //提交订单
    @GetMapping("/submitOrder")
    public boolean submitOrder(Long goodId, HttpServletRequest request) {
        try {
            String userId = request.getRemoteUser();
            if (userId != null) {
                seckillGoodsService.submitOrder(goodId, userId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
