package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination solrQueue;
    @Autowired
    private Destination solrDeleteQueue;
    @Autowired
    private Destination pageTopic;
    @Autowired
    private Destination pageDeleteTopic;

    //商品保存
    @PostMapping("/save")
    public boolean save(@RequestBody Goods goods) {
        try {
            //获取登陆用户名
            String sellerId = SecurityContextHolder.getContext().
                    getAuthentication().getName();
            goods.setSellerId(sellerId);
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //多条件分页查询
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        try {
            if (goods != null && !StringUtils.isEmpty(goods.getGoodsName())) {
                goods.setGoodsName(new String(goods.getGoodsName().
                        getBytes("ISO8859-1"), "UTF-8"));
            }
            //获取登陆用户名
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return goodsService.findByPage(goods, page, rows);
    }

    //批量上下架商品
      /*
      这里思考有点问题了,根据id来上下架基本不会有问题每个商家只是显示自己的商品而已!所以不需要商家id
      String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
      Goods goods = new Goods();
      goods.setSellerId(sellerId);
      goods.setIsMarketable(isMarketable);
     */
      //商家商品上下架修改可销售状态
      /**11.10加入了消息中间控件不是很明白,
       * 为什么同一个对象改一下变量名就能有不同的操作上课注意听
       * 11.16配置文件那边配了一个消息队列这里操作就发送消息到中间件的消息队列
       * */
    @GetMapping("/upMarketable")
    public boolean upMarketable(Long[] ids, String isMarketable) {
        try {
            //上下架操作
            goodsService.upMarketable(ids, isMarketable);
            //判断商品的上下架状态
            if ("1".equals(isMarketable)) {
                /**发送消息到中间件,在solr索引库中同步创建该商品的索引*/
                jmsTemplate.send(solrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        /**这里发送消息到中间件了,这里发送什么类型那边就要继承的时候确定类型*/
                        return session.createObjectMessage(ids);
                    }
                });
                /**发送消息生成静态网页ids可以在这里循环发送消息,也可以在接收那边循环处理*/
                for (Long goodsId : ids) {
                    jmsTemplate.send(pageTopic, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(goodsId.toString());
                        }
                    });
                }
            } else {//商品已经下架了
                /**发送消息,删除商品索引*/
                jmsTemplate.send(solrDeleteQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
                /**发送消息.删除静态网页*/
                jmsTemplate.send(pageDeleteTopic, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
