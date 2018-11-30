package com.pinyougou.mapper;

import com.pinyougou.pojo.Cities;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * CitiesMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface CitiesMapper extends Mapper<Cities> {

    @Select(" SELECT city FROM tb_cities WHERE cityid=#{cityId}")
    Cities findCityByCID(String cityId);
}