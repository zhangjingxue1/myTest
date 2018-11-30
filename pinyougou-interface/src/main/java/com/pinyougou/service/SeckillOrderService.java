package com.pinyougou.service;

import com.pinyougou.pojo.SeckillOrder;
import java.util.List;
import java.io.Serializable;
/**
 * SeckillOrderService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface SeckillOrderService {

	/** 添加方法 */
	void save(SeckillOrder seckillOrder);

	/** 修改方法 */
	void update(SeckillOrder seckillOrder);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	SeckillOrder findOne(Serializable id);

	/** 查询全部 */
	List<SeckillOrder> findAll();

	/** 多条件分页查询 */
	List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows);
	/**从redis中获取秒杀订单*/
    SeckillOrder findSeckillOrder(String userId);
	/**
	 * 支付成功保存订单
	 * @param userId 用户名
	 * @param transactionId 微信交易流水号
	 */
	void saveOrder(String userId, String transactionId);
	//查询所有超时未支付的订单
    List<SeckillOrder> findOrderByTimeOut();
	// 删除超时未支付的订单
	void deleteOrderFromRedis(SeckillOrder seckillOrder);
}