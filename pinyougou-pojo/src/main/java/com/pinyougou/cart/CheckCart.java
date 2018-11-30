package com.pinyougou.cart;

import com.pinyougou.pojo.OrderItem;

import java.io.Serializable;
import java.util.List;

public class CheckCart implements Serializable {

    // 商家ID
    private String sellerId;
    // 商家名称
    private String sellerName;
    // 选中购物车明细集合
    private List<OrderItem> checkItems;
    //是否商家选中
    private boolean isChecked;


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getCheckItems() {
        return checkItems;
    }

    public void setCheckItems(List<OrderItem> checkItems) {
        this.checkItems = checkItems;
    }
}
