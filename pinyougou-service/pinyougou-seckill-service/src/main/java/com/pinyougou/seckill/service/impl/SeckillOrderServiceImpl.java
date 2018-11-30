package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SeckillOrderService")
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 从redis中获取秒杀订单
     */
    @Override
    public SeckillOrder findSeckillOrder(String userId) {
        try {
            return (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 支付成功保存订单
     *
     * @param userId        用户名
     * @param transactionId 微信交易流水号
     */
    @Override
    public void saveOrder(String userId, String transactionId) {
        try {
            //根据用户ID从redis中查询秒杀订单
            SeckillOrder seckillOrder =
                    (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
            //判断秒杀订单
            if (seckillOrder != null) {
                //微信交易流水号
                seckillOrder.setTransactionId(transactionId);
                //支付时间
                seckillOrder.setPayTime(new Date());
                //状态码(已付款)
                seckillOrder.setStatus("1");
                //保存到数据库
                seckillOrderMapper.insertSelective(seckillOrder);
                //根据用户id删除Redis中的订单
                redisTemplate.boundHashOps("seckillOrderList").delete(userId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 查询所有超时未支付的订单
    @Override
    public List<SeckillOrder> findOrderByTimeOut() {
        try {
            //定义List集合封装超时5分钟未支付的订单
            List<SeckillOrder> seckillOrders = new ArrayList<>();
            //查询redis中所有未支付的订单
            List<SeckillOrder> seckillOrderList =
                    redisTemplate.boundHashOps("seckillOrderList").values();
            if (seckillOrderList != null && seckillOrderList.size() > 0) {
                for (SeckillOrder seckillOrder : seckillOrderList) {
                    //当前系统毫秒数-5分钟
                    long endTime = new Date().getTime() - (5 * 60 * 1000);
                    //判断创建的订单是否超出5分钟
                    if (seckillOrder.getCreateTime().getTime() < endTime) {
                        //把超时的订单添加到集合汇总
                        seckillOrders.add(seckillOrder);
                    }
                }
            }
            return seckillOrders;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 删除redis中超时未支付的订单
    @Override
    public void deleteOrderFromRedis(SeckillOrder seckillOrder) {
        try {
            //删除Redis缓存中的订单
            redisTemplate.boundHashOps("seckillOrderList").delete(seckillOrder.getUserId());
            /** ######## 恢复库存数量 #######*/
            // 从Redis查询秒杀商品
            SeckillGoods seckillGoods =
                    (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList")
                            .get(seckillOrder.getSeckillId());
            //判断缓存中是否存在该商品
            if (seckillGoods != null) {
                // 修改缓存中秒杀商品的库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            } else {//表示已经被秒光
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                //设置秒杀商品库存数量
                seckillGoods.setStockCount(1);
            }
            //存入缓存中
            redisTemplate.boundHashOps("seckillGoodsList")
                    .put(seckillOrder.getSeckillId(), seckillGoods);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }


}
