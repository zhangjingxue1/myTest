package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Brand;

import java.util.List;
import java.util.Map;

/**
 * BrandMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface BrandMapper extends Mapper<Brand> {

    //多条件查询品牌
    List<Brand> findAll(Brand brand);

    /**
     * 查询所有的品牌(id与name)
     */
    @Select("SELECT id, NAME AS text FROM tb_brand ORDER BY id ASC")
    List<Map<String, Object>> findAllByIdAndName();
}