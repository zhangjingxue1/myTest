package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference(timeout = 10000)
    private OrderService orderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 生成微信支付的二维码
     */
    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode(
            @RequestParam String orderId,
            @RequestParam String totalFee) {

        // 支付总金额（分）
        long money = (long) (Double.parseDouble(totalFee)* 100);
        //调用生成微信支付二维码服务的方法
        return weixinPayService.getPayCode(orderId, String.valueOf(money));
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo) {
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {
            //调用查询订单接口
            Map<String, String> resultMap =
                    weixinPayService.queryPayStatus(outTradeNo);
            //判断是否支付成功
            if (resultMap != null && resultMap.size() > 0) {
                //判断是否支付成功
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    //修改订单状态
                    orderService.updateOrder(outTradeNo,resultMap.get("transaction_id"));
                    data.put("status", 1);
                }
                //支付不成功
                if ("NOTPAY".equals(resultMap.get("trade_state"))) {
                    data.put("status", 2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    /**
     * 分页查询查询订单列表
     */
    @PostMapping("/findOrderByPage")
    public Map<String, Object> findOrderByPage(
            @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            return orderService.findOrderByPage(params, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
