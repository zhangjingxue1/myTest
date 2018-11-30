package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Areas;

/**
 * AreasMapper 数据访问接口
 * @date 2018-10-07 12:18:20
 * @version 1.0
 */
public interface AreasMapper extends Mapper<Areas>{

    @Select(" SELECT AREA FROM tb_areas WHERE areaid=#{areaId}")
    Areas findAreasByTID(String areaId);
}