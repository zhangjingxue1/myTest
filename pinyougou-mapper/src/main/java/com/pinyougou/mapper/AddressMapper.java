package com.pinyougou.mapper;

import com.pinyougou.pojo.Address;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/**
 * AddressMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface AddressMapper extends Mapper<Address> {

    @Update("UPDATE tb_address set is_default='0' WHERE user_id=#{username}")
    void updateAllDefault(String username);

    @Update("UPDATE tb_address set is_default='1' WHERE  id=#{id}")
    void updateDefaultAddress(String id);
}