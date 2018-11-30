package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {
    //注入数据访问接口代理对象
    @Autowired
    private BrandMapper brandMapper;

    //删除品牌(讲解时候需要理解以下代码以及Serializable[]的作用)
    /*controller传来的是Long ids,但是这里是Serializable[]接收的,为什么?
    因为实体类实现了序列化接口所以可以用序列化来接收.
    或者Object也行一般都使用Serializable[]接收通用化*/
    @Override
    public void deleteAll(Serializable[] ids) {
        //创建示范对象
        Example example = new Example(Brand.class);
        //创建条件对象
        Example.Criteria criteria = example.createCriteria();
       /*
        添加in条件,id in(?,?)第一个参数给实体类对应表的列,
        第二个参数给一个迭代器,List底层实现了迭代器,
        可以使用工具类Arrays.asList把数组转为List就可以使用了*/
        criteria.andIn("id", Arrays.asList(ids));
        //根据条件删除
        brandMapper.deleteByExample(example);
    }

    /**
     * 查询所有的品牌(id与name)
     */
    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return brandMapper.findAllByIdAndName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //查询所有品牌
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    //添加品牌
    @Override
    public void save(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    //修改品牌
    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    //分页查询品牌
    @Override
    public PageResult findByPage(Brand brand, int page, int rows) {
        //开始分页
        PageInfo<Brand> pageInfo = PageHelper.startPage(page, rows)
                .doSelectPageInfo(new ISelect() {
                    @Override
                    public void doSelect() {
                        brandMapper.findAll(brand);
                    }
                });
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


}
