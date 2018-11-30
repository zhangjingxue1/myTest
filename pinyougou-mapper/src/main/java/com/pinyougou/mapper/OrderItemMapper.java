package com.pinyougou.mapper;

import com.pinyougou.pojo.OrderItem;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * OrderItemMapper 数据访问接口
 * @date 2018-10-07 12:18:20
 * @version 1.0
 */
public interface OrderItemMapper extends Mapper<OrderItem>{
    /**根据订单id查询订单详情*/
    @Select("SELECT id,pic_path,goods_id,title,price,num,total_fee FROM tb_order_item WHERE order_id=#{orderId};")
    List<OrderItem> findOrderItemByOrderId(Long orderId);
}