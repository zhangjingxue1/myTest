package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.User;

/**
 * UserMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-07 12:18:20
 */
public interface UserMapper extends Mapper<User> {

    @Select("select * from tb_user where username=#{username}")
    User findUserByName(String username);

    @Update("UPDATE tb_user SET phone=#{newPhone} WHERE username=#{username};")
    void updatePhone(@Param("username") String username, @Param("newPhone") String newPhone);
}