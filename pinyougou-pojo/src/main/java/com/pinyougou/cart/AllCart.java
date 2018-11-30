package com.pinyougou.cart;

import java.io.Serializable;
import java.util.List;

public class AllCart implements Serializable {
    //购物车
    private List<Cart> carts;
    //是否全选0未选中,1选中
    private String isCheckedAll;

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public String getIsCheckedAll() {
        return isCheckedAll;
    }

    public void setIsCheckedAll(String isCheckedAll) {
        this.isCheckedAll = isCheckedAll;
    }
}
