package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference(timeout = 10000)
    private AddressService addressService;
    @Reference(timeout = 10000)
    private OrderService orderService;
    //微信支付服务接口
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 根据用户名查询用户收货地址
     */
    @GetMapping("/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            return addressService.findAddressByUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //提交订单
    @PostMapping("/save")
    public boolean save(@RequestBody Order order,
                        HttpServletRequest request) {
        try {
            //获取登陆用户名
            String username = request.getRemoteUser();
            order.setUserId(username);
            //设置订单来源PC端
            order.setSourceType("2");
            orderService.save(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成微信支付的二维码
     */
    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode(HttpServletRequest request) {
        //获取登陆的用户名
        String userId = request.getRemoteUser();
        //从redis查询支付日志
        PayLog payLog = orderService.findPayLogFromRedis(userId);
        //调用生成微信支付二维码服务的方法
        return weixinPayService.getPayCode(payLog.getOutTradeNo(),
                String.valueOf(payLog.getTotalFee()));
    }

    //查询支付状态
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
                    orderService.updateOrderStatus(outTradeNo,
                            resultMap.get("transaction_id"));
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
}
