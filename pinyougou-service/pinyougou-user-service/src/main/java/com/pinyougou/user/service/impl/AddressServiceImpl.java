package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户名查询用户收货地址
     */
    @Override
    public List<Address> findAddressByUser(String username) {
        try {
            //多做个默认地址置顶
            Example example = new Example(Address.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", username);
            example.setOrderByClause("is_default DESC");
            return addressMapper.selectByExample(example);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改为默认收获地址
     */
    @Override
    public boolean defaultAddress(String id, String username) {
        try {
            //默认收货地址只有一个,先要把用户所有收货地址全部变成非默认
            addressMapper.updateAllDefault(username);
            //然后再修改
            addressMapper.updateDefaultAddress(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加方法
     */
    @Override
    public void save(Address address) {
        try {
            addressMapper.insertSelective(address);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Address address) {
        try {
            addressMapper.updateByPrimaryKeySelective(address);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {
        try {
            addressMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

}
