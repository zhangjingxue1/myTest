package com.pinyougou.mapper;

import com.pinyougou.pojo.Seller;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface SellerMapper extends Mapper<Seller> {

    /**
     * 多条件分页查询商家列表
     */
    List<Seller> findByPage(Seller seller);

    /**
     * 审核修改状态码
     */
    @Update("UPDATE  tb_seller SET STATUS=#{status} WHERE NAME=#{name};")
    void checkPass(Seller seller);

    /**
     * 根据商家id查询商家店铺名称
     */
    @Select("SELECT nick_name FROM tb_seller WHERE seller_id=#{sellerId};")
    String findNickNameBySid(String sellerId);
}