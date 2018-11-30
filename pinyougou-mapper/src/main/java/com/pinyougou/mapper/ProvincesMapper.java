package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Provinces;

/**
 * ProvincesMapper 数据访问接口
 * @date 2018-10-07 12:18:20
 * @version 1.0
 */
public interface ProvincesMapper extends Mapper<Provinces>{

    @Select("SELECT province FROM tb_provinces WHERE provinceid=#{provinceId}")
    Provinces findProvinceByPID(String provinceId);
}