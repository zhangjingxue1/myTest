package com.pinyougou.mapper;

import com.pinyougou.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * OrderMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface OrderMapper extends Mapper<Order> {
    /**根据用户名查询订单列表*/
    @Select("SELECT create_time,order_id,seller_id,STATUS " +
            "FROM tb_order WHERE user_id=#{username} " +
            "ORDER BY create_time DESC")
    List<Order> findOrderByPage(String username);
    /**根据orderId查询订单*/
    @Select("select * from tb_order where order_id=#{orderId}")
    Order selectByOrderId(String orderId);

}