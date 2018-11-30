package com.pinyougou.service;

import com.pinyougou.cart.Cart;
import com.pinyougou.cart.CheckCart;

import java.util.List;

//购物车接口
public interface CartService {
    /**
     * 添加SKU商品到购物车
     *
     * @param carts  购物车(一个Cart对应一个商家)
     * @param itemId SKU商品id
     * @param num    购买数据
     * @return 修改后的购物车
     */
    List<Cart> addItemToCart(List<Cart> cartList,
                             Long itemId, Integer num);

    /**
     * 从redis中查询购物车
     *
     * @param username 用户名
     * @return List<Cart> 购物车
     */
    List<Cart> findCartRedis(String username);

    /**
     * 将购物车保存到redis中
     *
     * @param username 用户名
     * @param cartList 购物车
     */
    void saveCartRedis(String username, List<Cart> cartList);

    /**
     * 合并购物车
     *
     * @param cookieCarts cookie购物车
     * @param cartList    redis的购物车
     * @return 合并后的购物车
     */
    List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> cartList);

    /**
     * 添加到我的结算购物车
     */
    List<Cart> addMyCart(List<Cart> cartList, Long itemId, String username,
                         String sellerId, String status, List<CheckCart> myCart);

    /**根据用户名查询结算购物车*/
    List<CheckCart> findMyCartRedis(String username);

    /**商品全选*/
    List<Cart> sellerCheckAll(List<Cart> cart, List<CheckCart> myCart, String username, String sellerId, String status);
    /**全选*/
    List<Cart> checkAll(List<Cart> cartList, List<CheckCart> myCart, String status,String username);
    /**整合选中跟未选中的购物车*/
    List<Cart> closeCart(List<Cart> cartList, List<CheckCart> myCart);
}
