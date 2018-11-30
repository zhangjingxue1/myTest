package com.pinyougou.service;

import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.PayLog;

import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * OrderService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface OrderService {

	/** 添加方法 */
	void save(Order order);

	/** 修改方法 */
	void update(Order order);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Order findOne(Serializable id);

	/** 查询全部 */
	List<Order> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(Order order, int page, int rows);

	/**查询redis的支付日志*/
    PayLog findPayLogFromRedis(String userId);
	/**
	 * 修改订单状态
	 * @param outTradeNo 订单交易号
	 * @param transactionId 微信交易流水号
	 */
	void updateOrderStatus(String outTradeNo, String transactionId);


	/**分页查询订单列表*/
    Map<String,Object> findOrderByPage(Map<String, Object> params, String username);

	/**
	 * 修改订单状态
	 * @param outTradeNo 订单交易号
	 * @param transactionId 微信交易流水号
	 */
	void updateOrder(String outTradeNo, String transactionId );
}