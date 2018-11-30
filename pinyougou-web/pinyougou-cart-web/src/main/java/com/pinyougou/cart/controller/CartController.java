package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.cart.CheckCart;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 30000)
    private CartService cartService;
    //单例设置模式,但不是一个对象哦,因为request生命周期短.这个对象就结束了下次请求又是一个新的对象
    //每次请求对象都不一样的,线程安全的
    //如果是其他的属性就是一个对象,线程不安全的.
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 下面的方法是servlet调用的所以是线程安全的
     * 添加SKU商品到购物车
     */
    @GetMapping("/addCart")
    @CrossOrigin(origins = "http://item.pinyougou.com", allowCredentials = "true")
    public boolean addCart(Long itemId, Integer num) {
        try {
            /*//设置允许访问的域名(SpringMVC4.2以上版本就可以使用注解实现了)
            response.setHeader("Access-Control-Allow-Origin","http://item.pinyougou.com");
            //设置允许操作Cookie
            response.setHeader("Access-Control-Allow-Credentials","true");*/

            //获取登陆用名
            String username = request.getRemoteUser();
            //获取购物车集合
            List<Cart> cartList = findCart();
            //调用服务层添加SKU商品到购物车
            cartList = cartService.addItemToCart(cartList, itemId, num);
            if (StringUtils.isNoneBlank(username)) {
                //已登陆往Redis存储购物车
                cartService.saveCartRedis(username, cartList);
            } else {
                //未登录的用户往Cookies存储购物车
                //将购物车重新存入Cookies中
                CookieUtils.setCookie(request, response,
                        CookieUtils.CookieName.PINYOUGOU_CART,
                        JSON.toJSONString(cartList),
                        3600 * 24, true);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取购物车集合
     */
    @GetMapping("/findCart")
    public List<Cart> findCart() {
        //获取登陆用户名
        String username = request.getRemoteUser();
        //定义购物车集合
        List<Cart> cartList = null;
        //判断用户是否登陆
        if (StringUtils.isNoneBlank(username)) {
            //已经登陆了去redis获取购物车信息
            cartList = cartService.findCartRedis(username);
            //从Cookie中获取购物车集合Json字符串
            String cartStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);
            //判断是否为空
            if (StringUtils.isNoneBlank(cartStr)) {
                //转化为List集合
                List<Cart> cookieCarts = JSON.parseArray(cartStr, Cart.class);
                //判断在Cookies里面的购物车JSON字符串是否为空
                if (cookieCarts != null && cookieCarts.size() > 0) {
                    //合并购物车
                    cartList = cartService.mergeCart(cookieCarts, cartList);
                    //将合并后的购物车存入redis
                    cartService.saveCartRedis(username, cartList);
                    //删除掉cookie的购物车
                    CookieUtils.deleteCookie(request, response,
                            CookieUtils.CookieName.PINYOUGOU_CART);
                }
            }
        } else {
            //未登录从cookie获取购物车
            //从Cookie中获取购物车集合Json字符串
            String cartStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);
            //判断在Cookies里面的购物车JSON字符串是否为空
            if (StringUtils.isBlank(cartStr)) {
                //为了下面的JSON.parseArray转换是空的也会初始化一个变量出来.以免调用的时候空指针异常
                cartStr = "[]";
            }
            cartList = JSON.parseArray(cartStr, Cart.class);
        }

        //使用fastjson时出现$ref: "$[1].checkOrderItems[0]" 的解决办法（重复引用） 网址:https://www.jianshu.com/p/6041242405e8
        /*String jsonStr = JSON.toJSONString(cartList, SerializerFeature.DisableCircularReferenceDetect);
        cartList = JSON.parseArray(jsonStr, Cart.class);*/
        return cartList;
    }

    /**
     * 获取购物车集合
     */
    @GetMapping("/findCheckCart")
    public List<Cart> findCheckCart() {
        //获取登陆用户名
        String username = request.getRemoteUser();
        List<Cart> cartList=null;
        //已经登陆了去redis获取购物车信息
        cartList = cartService.findCartRedis(username);
        List<CheckCart> myCart = findMyCart();
        //整合两辆购物车并删除不相关内容
        cartList = cartService.closeCart(cartList,myCart);


        //使用fastjson时出现$ref: "$[1].checkOrderItems[0]" 的解决办法（重复引用） 网址:https://www.jianshu.com/p/6041242405e8
        /*String jsonStr = JSON.toJSONString(cartList, SerializerFeature.DisableCircularReferenceDetect);
        cartList = JSON.parseArray(jsonStr, Cart.class);*/
        return cartList;
    }

    /**
     * 加入我的购物车
     */
    @PostMapping("/addMyCart")
    public boolean addMyCart(Long itemId, String sellerId, String status) {
        try {
            //获取登陆用名
            String username = request.getRemoteUser();
            //获取购物车集合
            List<Cart> cartList = findCart();
            List<CheckCart> myCart = findMyCart();
            //添加商品到结算的购物车
            cartList = cartService.addMyCart(cartList, itemId, username, sellerId, status, myCart);


            //已登陆往Redis存储购物车
            cartService.saveCartRedis(username, cartList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取结算购物车集合
     */
    @GetMapping("/findMyCart")
    public List<CheckCart> findMyCart() {
        //获取登陆用户名
        String username = request.getRemoteUser();
        //定义购物车集合
        List<CheckCart> checkCartList = null;
        //判断用户是否登陆
        if (StringUtils.isNoneBlank(username)) {
            //已经登陆了去redis获取购物车信息
            checkCartList = cartService.findMyCartRedis(username);
        } else {
            //没登录就给他初始化一个数据什么都看不到算了,方便操作
            String cartStr = "[]";
            checkCartList = JSON.parseArray(cartStr, CheckCart.class);
        }
        return checkCartList;
    }

    /**
     * 商家全选
     */
    @GetMapping("/sellerCheckAll")
    public boolean sellerCheckAll(String sellerId, String status) {
        try {
            //获取登陆用名
            String username = request.getRemoteUser();
            //获取购物车集合
            List<Cart> cartList = findCart();
            List<CheckCart> myCart = findMyCart();
            //添加商品到结算的购物车
            cartList = cartService.sellerCheckAll(cartList, myCart, username, sellerId, status);

            //已登陆往Redis存储购物车
            cartService.saveCartRedis(username, cartList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 全选
     */
    @GetMapping("/checkAll")
    public boolean checkAll(String status) {
        try {
            //获取登陆用名
            String username = request.getRemoteUser();
            //获取购物车集合
            List<Cart> cartList = findCart();
            List<CheckCart> myCart = findMyCart();
            //添加商品到结算的购物车
            cartList = cartService.checkAll(cartList, myCart, status, username);

            //已登陆往Redis存储购物车
            cartService.saveCartRedis(username, cartList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
