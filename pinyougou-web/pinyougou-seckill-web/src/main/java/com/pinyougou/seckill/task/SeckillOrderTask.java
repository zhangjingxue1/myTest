package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 秒杀订单任务服务
 */
@Component
public class SeckillOrderTask {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * Spring-task定时调用该方法使用
     * 定时关闭超时未支付定单(每隔3秒调度一次)
     * Cron表达式是一个字符串，字符串分6个域，每一个域代表一个含义，Cron语法格式：
     * 	Seconds Minutes Hours DayofMonth Month DayofWeek
     * 【秒】   【分】   【小时】  【日】     【月】    【周】
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void closeOrderTask() {
        System.out.println("==毫秒==" + System.currentTimeMillis());
        //查询所有超时未支付的订单
        List<SeckillOrder> seckillOrderList = seckillOrderService.findOrderByTimeOut();
        //迭代超时未支付的订单
        for (SeckillOrder seckillOrder : seckillOrderList) {
            //关闭微信未支付订单
            Map<String, String> map =
                    weixinPayService.closePayTimeOut(seckillOrder.getId().toString());
            //如果正常关闭
            if ("SUCCESS".equals(map.get("result_code"))) {
                System.out.println("===超时，删除订单===");
                // 删除超时未支付的订单
                seckillOrderService.deleteOrderFromRedis(seckillOrder);
            }
        }


    }
}
