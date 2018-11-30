package com.pinyougou.mapper;

import com.pinyougou.pojo.Goods;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.GoodsDesc;

/**
 * GoodsDescMapper 数据访问接口
 * @date 2018-10-07 12:18:20
 * @version 1.0
 */
public interface GoodsDescMapper extends Mapper<GoodsDesc>{

    /**添加商品描述*/
    void save(Goods goods);
}