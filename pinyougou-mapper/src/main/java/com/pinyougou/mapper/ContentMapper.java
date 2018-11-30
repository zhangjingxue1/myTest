package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Content;

import java.util.List;

/**
 * ContentMapper 数据访问接口
 * @date 2018-10-07 12:18:20
 * @version 1.0
 */
public interface ContentMapper extends Mapper<Content>{

    @Select("select * from tb_content where category_id=#{contentId} " +
            "and status=1 order by sort_order asc")
    List<Content> findContentByCId(Long contentId);

}