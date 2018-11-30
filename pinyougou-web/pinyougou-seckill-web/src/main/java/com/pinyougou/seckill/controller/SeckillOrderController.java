package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class SeckillOrderController {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 生成微信支付的二维码
     */
    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode(HttpServletRequest request) {
        //获取登陆的用户名
        String userId = request.getRemoteUser();
        //从redis查询支付日志查询秒杀订单
        SeckillOrder seckillOrder = seckillOrderService.findSeckillOrder(userId);
        // 支付总金额（分）
        long totalFee = (long) (seckillOrder.getMoney().doubleValue() * 100);
        //调用生成微信支付二维码服务的方法
        return weixinPayService.getPayCode(seckillOrder.getId().toString(),
                String.valueOf(totalFee));
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo,
                                               HttpServletRequest request) {
        HashMap<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        Map<String, String> map = weixinPayService.queryPayStatus(outTradeNo);
        if (map != null && map.size() > 0) {
            //判断支付是否成功
            if ("SUCCESS".equals(map.get("trade_state"))) {
                //获取登陆用户名
                String userId = request.getRemoteUser();
                //把订单保存到数据库中
                seckillOrderService.saveOrder(userId, map.get("transaction_id"));
                data.put("status", 1);
            } else {
                data.put("status", 2);
            }
        }
        return data;
    }
}
