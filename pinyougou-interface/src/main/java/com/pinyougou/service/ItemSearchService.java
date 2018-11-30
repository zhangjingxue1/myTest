package com.pinyougou.service;

import com.pinyougou.pojo.SolrItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    //搜索方法
    Map<String, Object> search(Map<String, Object> params);
    /** 添加或修改商品索引 */
    void saveOrUpdate(List<SolrItem> solrItems);
    /**删除商品索引*/
    void delete(List<Long> goodsIds);
}
