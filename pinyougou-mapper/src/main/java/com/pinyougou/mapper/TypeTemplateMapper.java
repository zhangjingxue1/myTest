package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * TypeTemplateMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate> {

    /**
     * 多条件查询模版
     */
    List<TypeTemplate> findAll(TypeTemplate typeTemplate);
}