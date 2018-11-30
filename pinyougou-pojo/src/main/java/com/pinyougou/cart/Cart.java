package com.pinyougou.cart;

import com.pinyougou.pojo.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车实体
 *
 * @version 1.0
 * <p>File Created at 2018-04-26<p>
 */
public class Cart implements Serializable {
    // 商家ID
    private String sellerId;
    // 商家名称
    private String sellerName;
    // 未选中购物车订单明细集合
    private List<OrderItem> orderItems;
    //商家是否被选中,0未选中,1选中
    private String isChecked ;
    //是否全选0未选中,1选中
    private String isCheckedAll;

    /**
     * setter and getter method
     */
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getIsCheckedAll() {
        return isCheckedAll;
    }

    public void setIsCheckedAll(String isCheckedAll) {
        this.isCheckedAll = isCheckedAll;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}