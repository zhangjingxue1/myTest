package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SeckillGoodsService")
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询秒杀的商品集合(为啥这里要那么try catch代码块)
     */
    @Override
    public List<SeckillGoods> findSecKillGoods() {
        List<SeckillGoods> seckillGoodsList = null;
        try {
            // 从Redis中获取秒杀商品数据
            seckillGoodsList = redisTemplate.
                    boundHashOps("seckillGoodsList").values();
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                System.out.println("Redis缓存中数据：" + seckillGoodsList);
                return seckillGoodsList;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //审核通过条件
            criteria.andEqualTo("status", "1");
            //剩余的库存要大于0
            criteria.andGreaterThan("stockCount", 0);
            //开始的时间要小于当前时间
            criteria.andLessThanOrEqualTo("startTime", new Date());
            //结束的时间大于等于当前时间
            criteria.andGreaterThanOrEqualTo("endTime", new Date());
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

            try {
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps("seckillGoodsList")
                            .put(seckillGoods.getId(), seckillGoods);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return seckillGoodsList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询商品详情
     */
    @Override
    public SeckillGoods getSecKillGood(Long goodId) {
        try {
            return (SeckillGoods) redisTemplate.
                    boundHashOps("seckillGoodsList").get(goodId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 秒杀下单提交订单到Redis(保证线程安全)
     * synchronized:线程锁(单个进程相关)
     * 分布式锁(多个进程相关)Redis实现分布式锁
     * 多个进程都可以访问同一台Redis.
     *
     * 高并发 10w/s就用中间件队列里面排队处理,
     * 不用加锁利用中间件队列异步处理,
     */
    @Override
    public synchronized void submitOrder(Long goodId, String userId) {
        try {
            // 从Redis中查询秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.
                    boundHashOps("seckillGoodsList").get(goodId);
            // 判断库存数据
            if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
                // 减库存(redis)
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                // 判断是否已经被秒光
                if (seckillGoods.getNum() == 0) {
                    // 同步秒杀商品到数据库(修改库存)
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    // 删除Redis中的秒杀商品
                    redisTemplate.boundHashOps("seckillGoodsList").delete(goodId);
                } else {
                    // 重新存入Redis中
                    redisTemplate.boundHashOps("seckillGoodsList").put(goodId, seckillGoods);
                }
                SeckillOrder seckillOrder = new SeckillOrder();
                // 设置订单id
                seckillOrder.setId(idWorker.nextId());
                // 设置秒杀商品id
                seckillOrder.setSeckillId(goodId);
                // 设置秒杀价格
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                // 设置用户id
                seckillOrder.setUserId(userId);
                // 设置商家id
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                // 设置创建时间
                seckillOrder.setCreateTime(new Date());
                // 设置状态码(未付款)
                seckillOrder.setStatus("0");
                //保存订单到Redis
                redisTemplate.boundHashOps("seckillOrderList").put(userId, seckillOrder);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void save(SeckillGoods seckillGoods) {

    }

    @Override
    public void update(SeckillGoods seckillGoods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillGoods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillGoods> findAll() {
        return seckillGoodsMapper.selectAll();
    }

    @Override
    public List<SeckillGoods> findByPage(SeckillGoods seckillGoods, int page, int rows) {
        return null;
    }

}
