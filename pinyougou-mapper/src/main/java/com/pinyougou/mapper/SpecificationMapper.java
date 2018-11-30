package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Specification;

import java.util.List;
import java.util.Map;

/**
 * SpecificationMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface SpecificationMapper extends Mapper<Specification> {
    //多条件查询品牌
    List<Specification> findAll(Specification specification);

    //查询所有的规格id与name
    @Select("select id,spec_name as text from tb_specification order by id asc")
    List<Map<String,Object>> findAllByIdAndName();

}