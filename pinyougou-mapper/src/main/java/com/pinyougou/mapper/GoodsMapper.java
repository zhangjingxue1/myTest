package com.pinyougou.mapper;

import com.pinyougou.pojo.Goods;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface GoodsMapper extends Mapper<Goods> {
    /** 多条件查询商品 */
    List<Map<String, Object>> findByPage(Goods goods);

    /*
    以下这几个方法都可以优化掉的
    基本都是一样的只有列名不一样,这时候可以把列名动态设置成占位符,
    在mapper接口方法中用@Param这里用${参数的值}取出来代替表名或者列名
    这样才是${}的最正确用法.以下几个方法太过臃肿了,写成一个就好
    */
    void upStatusById(@Param("ids") Serializable[] ids,
                      @Param("auditStatus") String auditStatus);

    void upIsDeleteById(@Param("ids") Serializable[] ids,
                      @Param("isDelete") String isDelete);

    void upMarketable(@Param("ids") Long[] ids, @Param("isMarketable") String isMarketable);

}