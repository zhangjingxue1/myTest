package com.pinyougou.order.service.impl;

import java.util.Date;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.cart.Cart;
import com.pinyougou.cart.CheckCart;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.mapper.SellerMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PayLogMapper payLogMapper;
    @Autowired
    private SellerMapper sellerMapper;

    /**
     * 保存订单
     */
    @Override
    public void save(Order order) {
        try {


            //定义订单ID集合(一次支付对应多个订单)
            List<String> orderIdList = new ArrayList<>();
            //定义多个订单支付的总金额(元)
            double totalMoney = 0;

            //迭代购物车数据
            // 根据用户名获取Redis中购物车数据
            List<Cart> carts = (List<Cart>) redisTemplate
                    .boundValueOps("cart_" + order.getUserId()).get();
            //获取选中的购物车
            List<CheckCart> checkCarts = (List<CheckCart>)
                    redisTemplate.boundValueOps("checkCart" + order.getUserId()).get();

            //如果是全选状态把所有全选的结算了
            for (Cart cart : carts) {
                if ("1".equals(cart.getIsCheckedAll())) {
                    totalMoney = getTotalMoney(order, orderIdList, totalMoney, cart);
                }
              /*  这个会报错的!会触发ConcurrentModificationException异常。
              这就是你在操作这个对象的同时又去做另外一个操作，打个比方就是银行里你存了100块钱，你去取50块钱，这是在操作这个100块钱的对象，
              然后你媳妇又在同时去存50块钱，那这样她也在操作这100块钱的对象，所以就出现了问题，肯定是要操作完了才能做其他的操作吧。
                for (OrderItem orderItem : cart.getOrderItems()) {
                    //不是全选状态把选中的购物车
                    for (CheckCart checkCart : checkCarts) {
                        List<OrderItem> checkItems = checkCart.getCheckItems();
                        for (OrderItem checkItem : checkItems) {
                            if (checkItem.getItemId().equals(orderItem.getItemId())) {
                                oItems.add(orderItem);
                                cart.getOrderItems().remove(orderItem);
                            }
                        }
                    }
                }*/

                //不是全选就获取选中购物车的商品去购物车商品中删除掉
                Iterator<OrderItem> iterator = cart.getOrderItems().iterator();
                while (iterator.hasNext()) {
                    OrderItem next = iterator.next();
                    for (CheckCart checkCart : checkCarts) {
                        Iterator<OrderItem> checkItems = checkCart.getCheckItems().iterator();
                        while (checkItems.hasNext()) {
                            OrderItem checkItem = checkItems.next();
                            if (next.getItemId().equals(checkItem.getItemId())) {
                                iterator.remove();
                            }
                        }
                    }
                }
                totalMoney = getTotalMoney(order, orderIdList, totalMoney, cart);
                //判断是否为微信支付
                if ("1".equals(order.getPaymentType())) {
                    //创建支付日志对象
                    PayLog payLog = new PayLog();
                    //生成订单交易号
                    String outTradeNo = String.valueOf(idWorker.nextId());
                    //设置订单交易号
                    payLog.setOutTradeNo(outTradeNo);
                    //创建时间
                    payLog.setCreateTime(new Date());
                    //支付总金额(分)
                    payLog.setTotalFee((long) (totalMoney * 100));
                    //用户id
                    payLog.setUserId(order.getUserId());
                    //支付状态
                    payLog.setTradeState("0");
                    //订单号集合,逗号分隔
                    String ids = orderIdList.toString()
                            .replace("[", "")
                            .replace("]", "")
                            .replace(" ", "");
                    //设置订单号
                    payLog.setOrderList(ids);
                    //支付类型
                    payLog.setPayType("1");
                    //往支付日志表插入数据(这里设置的不合理吧,
                    //只是方便自己获取订单的支付日志,不方便以后扩展啊,
                    //感觉应该根据订单id存取但是会有多个订单)
                    payLogMapper.insertSelective(payLog);
                    //存入缓存
                    redisTemplate.boundValueOps("payLog_" +
                            order.getUserId()).set(payLog);
                    //全选就删除该用户购物车中所有的数据
                    if ("1".equals(cart.getIsCheckedAll())) {
                        redisTemplate.delete("cart_" + order.getUserId());
                    }
                }
            }
            //不是全选就重写设置新的值
            redisTemplate.boundValueOps("cart_" + order.getUserId()).set(carts);
            //最终都要删除选中购物车
            redisTemplate.delete("checkCart" + order.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取支付总金额
     */
    private double getTotalMoney(Order order, List<String> orderIdList, double totalMoney, Cart cart) {
        /** 往订单表插入数据利用idWorder */
        //生成订单主键id
        long orderId = idWorker.nextId();
        Order order1 = new Order();
        // 设置订单id
        order1.setOrderId(orderId);
        // 设置支付类型
        order1.setPaymentType(order.getPaymentType());
        // 设置支付状态码为“未支付”
        order1.setStatus("1");
        // 设置订单创建时间
        order1.setCreateTime(new Date());
        // 设置订单修改时间
        order1.setUpdateTime(order1.getCreateTime());
        // 设置用户名
        order1.setUserId(order.getUserId());
        // 设置收件人地址
        order1.setReceiverAreaName(order.getReceiverAreaName());
        // 设置收件人手机号码
        order1.setReceiverMobile(order.getReceiverMobile());
        // 设置收件人
        order1.setReceiver(order.getReceiver());
        // 设置订单来源
        order1.setSourceType(order.getSourceType());
        // 设置商家id
        order1.setSellerId(cart.getSellerId());

        //定义该订单的总金额
        double money = 0;
        //往订单明细表插入数据
        for (OrderItem orderItem : cart.getOrderItems()) {
            //设置主键id
            orderItem.setId(idWorker.nextId());
            //设置关联的订单id
            orderItem.setOrderId(orderId);
            //累积总金额
            money += orderItem.getTotalFee().doubleValue();
            //保存到订单明细表数据库
            orderItemMapper.insertSelective(orderItem);
        }
        //设置支付总金额
        order1.setPayment(new BigDecimal(money));
        //保存数据到订单表
        orderMapper.insertSelective(order1);
        //记录订单id
        orderIdList.add(String.valueOf(orderId));
        //记录总金额
        totalMoney += money;
        return totalMoney;
    }

    /**
     * 根据用户id从redis中获取数据
     */
    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try {
            return (PayLog) redisTemplate.boundValueOps("payLog_" + userId).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改订单状态
     *
     * @param outTradeNo    订单交易号
     * @param transactionId 微信交易流水号
     */
    @Override
    public void updateOrderStatus(String outTradeNo,
                                  String transactionId) {
        /**修改支付日志状态*/
        PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
        payLog.setPayTime(new Date());//支付时间
        payLog.setTradeState("1");//标记为已支付状态
        payLog.setTransactionId(transactionId);//交易流水号
        //修改支付日志表
        payLogMapper.updateByPrimaryKeySelective(payLog);
        /**修改订单状态*/
        String[] orderIds = payLog.getOrderList().split(",");//订单号列表
        //循环订单号数组
        for (String orderId : orderIds) {
            Order order = new Order();
            order.setOrderId(Long.valueOf(orderId));//订单号
            order.setPaymentTime(new Date());//支付时间
            order.setStatus("2");//已支付
            orderMapper.updateByPrimaryKeySelective(order);
        }
        /**支付完之后清空redis的存储数据*/
        redisTemplate.delete("payLog_" + payLog.getUserId());

    }

    /**
     * 分页查询订单列表根据用户名
     */
    @Override
    public Map<String, Object> findOrderByPage(
            Map<String, Object> params,
            String username) {
        //创建Map集合封装返回数据
        HashMap<String, Object> data = new HashMap<>();

        /**分页*/
        //获取当前页码
        Integer page = (Integer) params.get("page");
        //第一次访问page是空
        if (page == null || page < 1) {
            //默认是第一页
            page = 1;
        }
        //获取每页显示的记录数据
        Integer rows = (Integer) params.get("rows");
        //第一次访问每页大小是空
        if (rows == null) {
            //默认是5个
            rows = 3;
        }
        //用分页插件查询订单列表
        PageInfo<Order> pageInfo = PageHelper.startPage(page, rows)
                .doSelectPageInfo(new ISelect() {
                    @Override
                    public void doSelect() {
                        orderMapper.findOrderByPage(username);
                    }
                });
        //根据查询出来订单列表用订单id查询订单详情
        List<Order> orderList = pageInfo.getList();
        for (Order order : orderList) {
            //根据订单id查询订单详情
            List<OrderItem> orderItems = orderItemMapper.
                    findOrderItemByOrderId(order.getOrderId());
            //转换long为String
            order.setOrderSid(order.getOrderId().toString());
            //封装订单详情数据
            order.setOrderItems(orderItems);
            //根据商家id查询商家店铺名称
            String nickName = sellerMapper.findNickNameBySid(order.getSellerId());
            //封装店铺名称
            order.setNickName(nickName);
        }
        //获取内容
        data.put("model", orderList);
        //设置总页数
        data.put("totalPages", pageInfo.getPages());
        //设置总记录数
        data.put("total", pageInfo.getTotal());

        return data;
    }

    /**
     * 更新订单状态修改为已支付并且修改支付日志表(这里应该是查出来的,
     * 但是因为之前练习购买的时候很多订单保存的时候没有插入支付日志表,
     * 所以没有数据直接新建支付日志算了免得找不到空指针异常!)
     *
     * @param outTradeNo    订单交易号
     * @param transactionId 微信交易流水号
     */
    @Override
    public void updateOrder(String outTradeNo, String transactionId) {
        try {
            //根据订单号修改订单为已支付状态
            Order order = orderMapper.selectByOrderId(outTradeNo);
            order.setStatus("2");
            order.setPaymentTime(new Date());
            order.setUpdateTime(new Date());
            //新增支付日志记录

            PayLog payLog = new PayLog();
            IdWorker idWorker = new IdWorker();
            payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
            payLog.setCreateTime(new Date());
            payLog.setPayTime(new Date());
            payLog.setUserId(order.getUserId());
            payLog.setTransactionId(transactionId);
            payLog.setTradeState("1");
            payLog.setPayType("1");
            //往支付日志表插入数据;
            payLogMapper.insertSelective(payLog);
            //修改订单表状态
            orderMapper.updateByPrimaryKeySelective(order);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Order order, int page, int rows) {
        return null;
    }
}
