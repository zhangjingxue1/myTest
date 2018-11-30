package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.cart.CheckCart;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 添加到我的结算购物车
     * 购物车视图展示,结算购物车做数据保存
     */
    @Override
    public List<Cart> addMyCart(List<Cart> cartList,
                                Long itemId, String username,
                                String sellerId, String status,
                                List<CheckCart> myCart) {
        try {
            //先获取商家未结算的购物车
            Cart cart = searchCartBySellerId(cartList, sellerId);
            //根据商品id在商家未结算购物车中得到商品
            OrderItem orderItem = searchOrderByItemId(cart.getOrderItems(), itemId);
            //修改商品的的选中状态
            orderItem.setIsChecked(status);

            //把选中的商品加入到选中购物车中
            if (myCart != null && myCart.size() > 0) {
                //根据商家id查询结算购物车获取商家购物车
                CheckCart checkCart = searchCheckCartBySellerId(myCart, sellerId);
                //如果里面有选中商品
                if (checkCart != null) {
                    //获取商品集合把选中的商品加入到结算购物车中
                    List<OrderItem> checkItems = checkCart.getCheckItems();
                    //如果选中则加入一个商品到结算购物车
                    if ("1".equals(orderItem.getIsChecked())) {
                        checkItems.add(orderItem);
                    } else {
                        OrderItem orderItems = searchOrderByItemId(checkItems, itemId);
                        //没选中则删除掉结算购物车对应的商品
                        checkItems.remove(orderItems);
                        // 如果cart的orderItems订单明细为0，则删除cart
                        if (checkItems.size() == 0) {
                            //从用户的购物车集合中删除商家的购物车
                            myCart.remove(checkCart);
                        }
                    }
                    //商家全选设置如果未选中的购物车集合不等于选中的购物车集合长度就设置为0商家未全选中状态
                    if (checkCart.getCheckItems().size() != cart.getOrderItems().size()) {
                        //设置为商家为未全选中状态
                        cart.setIsChecked("0");
                        //设置全选为未选中状态
                        for (Cart cat : cartList) {
                            cat.setIsCheckedAll("0");
                        }
                    } else {
                        //设置为商家全选中状态
                        cart.setIsChecked("1");
                        for (Cart cat : cartList) {
                            cat.setIsCheckedAll("1");
                        }
                    }
                } else {//没车自己造
                    //创建一个新的购物车对象
                    checkCart = createCheckCart(sellerId, cart, orderItem);
                    //封装结算购物车到集合中
                    myCart.add(checkCart);
                }
            } else {//没车自己造
                //创建一个集合准备封装选中购物车数据
                myCart = new ArrayList<>();
                //创建一个新的购物车对象
                CheckCart checkCart = createCheckCart(sellerId, cart, orderItem);
                //封装结算购物车到集合中
                myCart.add(checkCart);
                //商家全选设置如果未选中的购物车集合不等于选中的购物车集合长度就设置为0商家未全选中状态
                if (checkCart.getCheckItems().size() != cart.getOrderItems().size()) {
                    cart.setIsChecked("0");
                } else {
                    //设置为商家全选中状态
                    cart.setIsChecked("1");
                }
            }
            //把结算购物车加入到redis储存
            redisTemplate.boundValueOps("checkCart" + username).set(myCart);
            return cartList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建结算购物车
     */
    private CheckCart createCheckCart(String sellerId, Cart cart, OrderItem orderItem) {
        CheckCart checkCart = new CheckCart();
        //封装对象商家id
        checkCart.setSellerId(sellerId);
        //封装商家名
        checkCart.setSellerName(cart.getSellerName());
        //创建集合准备封装选中商品的数据
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        checkCart.setCheckItems(orderItems);
        return checkCart;
    }


    /**
     * 根据用户名查询结算购物车
     */
    @Override
    public List<CheckCart> findMyCartRedis(String username) {
        //从redis中获取购物车
        List<CheckCart> cartList = (List<CheckCart>) redisTemplate.
                boundValueOps("checkCart" + username).get();
        //如果是空的话会报错的,所以要new一个出来,因为前端已经调用了这个对象了
        if (cartList == null) {
            cartList = new ArrayList<>(0);
        }
        return cartList;
    }

    /**
     * 商品全选
     */
    @Override
    public List<Cart> sellerCheckAll(List<Cart> cartList,
                                     List<CheckCart> myCart,
                                     String username,
                                     String sellerId,
                                     String status) {
        //先获取商家未结算的购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //设置全选状态
        cart.setIsChecked(status);
        //设置商品集合为选中状态
        List<OrderItem> orderItems = cart.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setIsChecked(status);
        }
        //获取商家对应的选中购物车
        CheckCart checkCart = searchCheckCartBySellerId(myCart, sellerId);

        //购物车是否存在
        if (myCart != null && myCart.size() > 0) {
            //如果购物车存在先删除其他选中的
            myCart.remove(checkCart);
            //判断如果是选中则设置都是为选中
            if ("1".equals(status)) {
                //重新创建一个选中购物车
                checkCart = new CheckCart();
                //封装对象商家id
                checkCart.setSellerId(sellerId);
                //封装商家名
                checkCart.setSellerName(cart.getSellerName());
                //封装集合(可能抛空针异常)
                checkCart.setCheckItems(orderItems);
                //封装结算购物车到集合中
                myCart.add(checkCart);
            } else {
                myCart.remove(checkCart);
            }
        } else {//商家购物不存在
            //判断如果是选中则设置都是为选中
            if ("1".equals(status)) {
                //创建一个集合准备封装选中购物车数据
                myCart = new ArrayList<>();
                //创建一个新的购物车对象
                checkCart = new CheckCart();
                //封装对象商家id
                checkCart.setSellerId(sellerId);
                //封装商家名
                checkCart.setSellerName(cart.getSellerName());
                //封装集合(可能抛空针异常)
                checkCart.setCheckItems(orderItems);
                //封装结算购物车到集合中
                myCart.add(checkCart);
            } else {
                //未选中状态自己删自己
                myCart.remove(checkCart);
            }
        }
        //判断全选按钮
        if (cartList.size() == myCart.size()) {
            for (Cart ccc : cartList) {
                ccc.setIsCheckedAll("1");
            }
        } else {
            for (Cart ccc : cartList) {
                ccc.setIsCheckedAll("0");
            }
        }
        //把结算购物车加入到redis储存
        redisTemplate.boundValueOps("checkCart" + username).set(myCart);

        return cartList;
    }

    /**
     * 全选
     */
    @Override
    public List<Cart> checkAll(List<Cart> cartList, List<CheckCart> myCart, String status, String username) {
        //全选就把选中购物车的自己给杀掉.
        myCart.removeAll(myCart);

        //全选状态下把所有元素都设置为1
        for (Cart cart : cartList) {
            cart.setIsCheckedAll(status);
            cart.setIsChecked(status);
            for (OrderItem orderItem : cart.getOrderItems()) {
                orderItem.setIsChecked(status);
            }
        }
        //把结算购物车加入到redis储存
        redisTemplate.boundValueOps("checkCart" + username).set(myCart);
        return cartList;
    }
    /**整合购物车*/
    @Override
    public List<Cart> closeCart(List<Cart> cartList, List<CheckCart> myCart) {
        //如果是全选状态把所有全选的结算了
        for (Cart cart : cartList) {
            if ("1".equals(cart.getIsCheckedAll())){
                return cartList;
            }
           /* for (OrderItem orderItem : cart.getOrderItems()) {
                //不是全选状态把选中的购物车封装成购物车返回
                for (CheckCart checkCart : myCart) {
                    List<OrderItem> checkItems = checkCart.getCheckItems();
                    for (OrderItem checkItem : checkItems) {
                        if (!checkItem.getItemId().equals(orderItem.getItemId())){
                            //这里暂时删除掉不是选中的商品等结算的时候就真的删除掉
                            cart.getOrderItems().remove(orderItem);
                        }
                    }
                }
            }*/

            Iterator<OrderItem> iterator = cart.getOrderItems().iterator();
            while (iterator.hasNext()) {
                OrderItem next = iterator.next();
                for (CheckCart checkCart : myCart) {
                    Iterator<OrderItem> checkItems = checkCart.getCheckItems().iterator();
                    while (checkItems.hasNext()) {
                        OrderItem checkItem = checkItems.next();
                        if (!next.getItemId().equals(checkItem.getItemId())) {
                            //这里暂时删除掉不是选中的商品等结算的时候就真的删除掉
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return cartList;
    }


    /**
     * 从redis中查询购物车
     *
     * @param username 用户名
     * @return List<Cart> 购物车
     */
    @Override
    public List<Cart> findCartRedis(String username) {
        //从redis中获取购物车
        List<Cart> cartList = (List<Cart>) redisTemplate.
                boundValueOps("cart_" + username).get();
        //如果是空的话会报错的,所以要new一个出来,因为前端已经调用了这个对象了
        if (cartList == null) {
            cartList = new ArrayList<>(0);
        }
        return cartList;
    }

    /**
     * 将购物车保存到redis中
     *
     * @param username 用户名
     * @param cartList 购物车
     */
    @Override
    public void saveCartRedis(String username, List<Cart> cartList) {
        redisTemplate.boundValueOps("cart_" + username).set(cartList);
    }

    /**
     * 添加SKU商品到购物车
     *
     * @param cartList 购物车(一个Cart对应一个商家)
     * @param itemId   SKU商品id
     * @param num      购买数据
     * @return 修改后的购物车
     */
    @Override
    public List<Cart> addItemToCart(List<Cart> cartList,
                                    Long itemId, Integer num) {
        try {
            //根据SKU商品id查询SKU商品对象
            Item item = itemMapper.selectByPrimaryKey(itemId);
            //获取商家id
            String sellerId = item.getSellerId();
            //根据商家ID判断购物车集合中是否存在该商家的商品列表
            Cart cart = searchCartBySellerId(cartList, sellerId);
            //判断cart(该用户是否购买过该商家商品)
            if (cart == null) {
                //如果没有购买过该商家的商品
                //创建一个新的购物车
                cart = new Cart();
                //设置商家id
                cart.setSellerId(sellerId);
                //设置商家名称
                cart.setSellerName(item.getSeller());
                //创建订单明细(购物车中的一个商品)
                OrderItem orderItem = createOrderItem(item, num);
                //创建购物车列表目前是空的
                List<OrderItem> orderItems = new ArrayList<>();

                //往购物车列表中添加商品
                orderItems.add(orderItem);
                //为购物车设置订单明细集合
                cart.setOrderItems(orderItems);

                //设置商家为未选中状态"0"未选中
                cart.setIsChecked("0");

                //将商家的的购物车对象添加到用户的购物车集合中
                cartList.add(cart);
            } else {
                //购买过该商家的商品
                //判断是否购买过同样的商品(根据itemId到商家购物车列表中查询订单明细)
                OrderItem orderItem = searchOrderByItemId(cart.getOrderItems(), itemId);
                //如果没有购买过相同的商品
                if (orderItem == null) {
                    //创建新的购物车订单明细
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItems().add(orderItem);
                } else {
                    //如果有,在原购物车订单明细上添加数量,更改金额
                    orderItem.setNum(orderItem.getNum() + num);
                    //重新计算总计金额这里要用本来的购买数量orderItem.getNum(),用新的不符合逻辑
                    orderItem.setTotalFee(new BigDecimal(
                            orderItem.getPrice().doubleValue() * orderItem.getNum()));
                    //如果订单明细的购买数量小于等于0,则删除
                    if (orderItem.getNum() <= 0) {
                        //从商家的购物车列表中删除该商品
                        //删除购物车中的订单明细(商品)
                        cart.getOrderItems().remove(orderItem);
                    }
                    // 如果cart的orderItems订单明细为0，则删除cart
                    if (cart.getOrderItems().size() == 0) {
                        //从用户的购物车集合中删除商家的购物车
                        cartList.remove(cart);
                    }
                }
            }
            return cartList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 合并购物车
     *
     * @param cookieCarts cookie购物车
     * @param cartList    redis的购物车
     * @return 合并后的购物车
     */
    @Override
    public List<Cart> mergeCart(List<Cart> cookieCarts,
                                List<Cart> cartList) {
        //迭代Cookie中购物车的数据
        for (Cart cookieCart : cookieCarts) {
            //获取到订单明细
            List<OrderItem> orderItems = cookieCart.getOrderItems();
            //迭代订单明细
            for (OrderItem orderItem : orderItems) {
                //合并的时候添加SKU商品到购物车
                cartList = addItemToCart(cartList,
                        orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList;
    }

    /**
     * 根据itemId到商家购物车列表中查询订单明细
     */
    private OrderItem searchOrderByItemId(
            List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 从购物车集合中获取该商家的购物车
     */
    private Cart searchCartBySellerId
    (List<Cart> cartList, String sellerId) {
        //迭代购物车集合如果购物车的商品的商家id跟商家的id相同就返回购物车
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 从结算购物车集合中获取该商家的购物车
     */
    private CheckCart searchCheckCartBySellerId
    (List<CheckCart> cartList, String sellerId) {
        //迭代购物车集合如果购物车的商品的商家id跟商家的id相同就返回购物车
        for (CheckCart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    //创建未选中订单明细(购物车中的一个商品)
    //因为这个表没有购买数量那些数据只能把item跟前端获取的数据封装成订单明细
    private OrderItem createOrderItem(Item item, Integer num) {
        //创建订单明细
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        //默认新增的商品未选中
        orderItem.setIsChecked("0");
        return orderItem;
    }


}
