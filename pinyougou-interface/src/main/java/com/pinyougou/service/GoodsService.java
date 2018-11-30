package com.pinyougou.service;

import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.PageResult;

import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * GoodsService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface GoodsService {

	/** 添加方法 */
	void save(Goods goods);

	/** 修改方法 */
	void update(Goods goods);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Goods findOne(Serializable id);

	/** 查询全部 */
	List<Goods> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(Goods goods, int page, int rows);
	/**通过id修改状态码*/
    void upStatusById(Serializable[] ids, String auditStatus);
    /**批量操作商品上下架*/
    void upMarketable(Long[] ids, String isMarketable);
	/**根据id查询商品信息*/
    Map<String,Object> getGoods(Long goodsId);
	/**查询已经上架的SKU商品数据*/
	List<Item> findItemByGoodsId(Long[] ids);
}