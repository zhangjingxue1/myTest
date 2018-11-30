package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;

@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;


    //添加商品
    @Override
    public void save(Goods goods) {
        try {
            //添加商品新增商品默认为未审核状态0
            goods.setAuditStatus("0");
            //默认设置为未上架状态0
            goods.setIsMarketable("0");
            //添加spu商品表
            goodsMapper.insertSelective(goods);

            //添加商品描述
            GoodsDesc goodsDesc = goods.getGoodsDesc();
            //设置主键id,只有SPU商品表添加才会有商品id否则这里是空的操作不了
            goodsDesc.setGoodsId(goods.getId());

            goodsDescMapper.insertSelective(goodsDesc);
            //判断是否启用规格
            if ("1".equals(goods.getIsEnableSpec())) {
                /** 迭代所有的SKU具体商品集合，往SKU表插入数据 */
                //前端传来的数据不完整,有些数据需要自己设置
                List<Item> items = goods.getItems();
                for (Item item : items) {
                    // {spec : {}, isDefault : '0', status : '0', price : 0, num : 9999}
                    // SKU商品的标题 = SPU标题 + 规格选项
                    StringBuilder title = new StringBuilder();
                    title.append(goods.getGoodsName());
                    //把规格选项JSON字符串转化为MAP集合({"网络":"电信4G","机身内存","16G"})
                    Map<String, Object> spec = JSON.parseObject(item.getSpec());
                    for (Object optionName : spec.values()) {
                        //拼接规格选项到SKU商品标题
                        title.append(" " + optionName);
                    }
                    //设置SKU商品的标题
                    item.setTitle(title.toString());
                    //设置SKU商品其他属性
                    setItemInfo(item, goods);
                    //调用数据访问层保存数据
                    itemMapper.insertSelective(item);
                }
            } else { //如果不启动规格则使用默认值SPU就是SKU只在tb_item表中插入一条数据
                //创建SKU具体商品对象
                Item item = new Item();
                //设置SKU商品的标题
                item.setTitle(goods.getGoodsName());
                //设置SKU商品的价格
                item.setPrice(goods.getPrice());
                //设置SKU商品库存数据
                item.setNum(9999);
                //设置SKU商品的启动状态
                item.setStatus("1");
                //设置是否默认
                item.setIsDefault("1");
                //设置规格选项
                item.setSpec("{}");
                //设置SKU商品的其他属性
                setItemInfo(item, goods);
                //调用数据访问层保存数据
                itemMapper.insertSelective(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //封装SKU其他商品属性的方法
    private void setItemInfo(Item item, Goods goods) {
        //设置SKU商品的图片地址
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            //取第一张图片
            item.setImage((String) imageList.get(0).get("url"));
        }

        //设置SKU商品的分类(三级分类)
        item.setCategoryid(goods.getCategory3Id());
        //设置SKU商品的创建时间
        item.setCreateTime(new Date());
        //设置SKU商品的修改时间
        item.setUpdateTime(item.getCreateTime());
        //设置SPU商品的编号
        item.setGoodsId(goods.getId());
        //设置商家编号
        item.setSellerId(goods.getSellerId());
        //设置商品分类名称
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        //保险一点万一查出来是空的保存的商品分类名称就没有值了
        item.setCategory(itemCat != null ? itemCat.getName() : "");
        //设置商家店铺的名称
        Seller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller != null ? seller.getNickName() : "");
        //设置品牌名称
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand != null ? brand.getName() : "");
    }

    @Override
    public void update(Goods goods) {

    }

    /**
     * 删除方法
     */
    @Override
    public void delete(Serializable id) {

    }

    /**
     * 删除商品
     */
    @Override
    public void deleteAll(Serializable[] ids) {
        /*
        需求理解错误不是要删除掉商品是要把is_delete修改为1
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        goodsMapper.deleteByExample(example);*/
        goodsMapper.upIsDeleteById(ids, "1");
    }

    @Override
    public Goods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Goods> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     */
    @Override
    public PageResult findByPage(Goods goods, int page, int rows) {
        try {
            //为了性能好页面上需要什么数据就查询什么数据这样的话就用Map集合就好了,select*就List
            PageInfo<Map<String, Object>> pageInfo = PageHelper.startPage(page, rows).
                    doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            goodsMapper.findByPage(goods);
                        }
                    });
            //循环查询到的商品
            for (Map<String, Object> map : pageInfo.getList()) {
                ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");
                ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");
                ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 审核方法
     */
    @Override
    public void upStatusById(Serializable[] ids, String auditStatus) {
        try {
            goodsMapper.upStatusById(ids, auditStatus);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上下架方法
     */
    @Override
    public void upMarketable(Long[] ids, String isMarketable) {
        try {
            goodsMapper.upMarketable(ids, isMarketable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取商品详细信息
     */
    @Override
    public Map<String, Object> getGoods(Long goodsId) {
        try {
            //定义一个数据模型Map封装数据
            HashMap<String, Object> dataModel = new HashMap<>();
            //调用商品mapper根据主键id查询商品
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            //调用商品描述mapper根据主键id查询商品
            GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //封装数据给map返回数据
            dataModel.put("goods", goods);
            dataModel.put("goodsDesc", goodsDesc);
            /**商品分类*/
            if (goods != null && goods.getCategory3Id() != null) {
                String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
                String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
                String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
                dataModel.put("itemCat1", itemCat1);
                dataModel.put("itemCat2", itemCat2);
                dataModel.put("itemCat3", itemCat3);
            }
            /**SKU的数据*/
            Example example = new Example(Item.class);
            Example.Criteria criteria = example.createCriteria();

            //条件状态码为1
            criteria.andEqualTo("status", "1");
            //条件: SPU ID
            criteria.andEqualTo("goodsId", goodsId);
            //按是否默认降序(保证第一个为默认)
            example.orderBy("isDefault").desc();

            //条件查询SKU商品数据
            List<Item> itemList = itemMapper.selectByExample(example);
            //把itemList转换为JSON字符串封装到Map,方便在页面${itemList}取出
            dataModel.put("itemList", JSON.toJSONString(itemList));
            return dataModel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 查询上架的SKU商品数据
     */
    @Override
    public List<Item> findItemByGoodsId(Long[] ids) {
        try {
            //创建示范对象
            Example example = new Example(Item.class);
            //创建查询条件
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status","1");
            //加入in查询条件select * from tb_item where id in(?,?,?.....);
            criteria.andIn("goodsId", Arrays.asList(ids));
            //查询数据即可
            return itemMapper.selectByExample(example);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
