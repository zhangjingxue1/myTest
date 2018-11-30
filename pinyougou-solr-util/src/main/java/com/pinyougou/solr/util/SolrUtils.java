package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**把tb_item表中的数据导入solr服务器的索引库中*/
@Component
public class SolrUtils {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemMapper itemMapper;

    /**
     * 导入商品数据
     */
    public void importItemData() {
        //创建条件对象
        Item item = new Item();
        //设置只查询上架的商品
        item.setStatus("1");
        //查询列表
        List<Item> itemList = itemMapper.select(item);
        System.out.println("===商品列表===");

        //创建solr集合
        ArrayList<SolrItem> solrItems = new ArrayList<>();
        for (Item item1 : itemList) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setBrand(item1.getBrand());
            solrItem.setCategory(item1.getCategory());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setImage(item1.getImage());
            solrItem.setPrice(item1.getPrice());
            solrItem.setSeller(item1.getSeller());
            solrItem.setTitle(item1.getTitle());
            solrItem.setUpdateTime(item1.getUpdateTime());

            //将spec字段json字符串转为map
            Map specMap = JSON.parseObject(item1.getSpec(), Map.class);
            //设置动态域
            solrItem.setSpecMap(specMap);

            solrItems.add(solrItem);
        }
        /**保存数据到索引库*/
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus()==0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
        System.out.println("===结束===");
    }

    //导入商品数据
    public static void main(String[] args) {
        //读取配置文件
        ApplicationContext context =
                new ClassPathXmlApplicationContext
                        ("classpath:applicationContext.xml");
        //把自己加载到bean容器中
        SolrUtils solrUtils = context.getBean(SolrUtils.class);
        //获取商品数据
        solrUtils.importItemData();
    }
}
