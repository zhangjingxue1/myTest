package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.SpecificationService")
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    //注入数据访问接口代理对象
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * 添加规格
     */
    @Override
    public void save(Specification specification) {
        try {
            // 往tb_specification规格表插入数据
            specificationMapper.insertSelective(specification);

            // 往tb_specification_option规格选项表插入数据(多行)
            // 循环的话性能就比较差了,用一条语句即可
            specificationOptionMapper.save(specification);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新保存
     */
    @Override
    public void update(Specification specification) {
        try {
            // 修改规格表数据
            specificationMapper.updateByPrimaryKeySelective(specification);
            /**########### 修改规格选项表数据 ###########*/
            // 第一步：删除规格选项表中的数据 spec_id
            // delete from tb_specification_option where spec_id = ?
            // 创建规格选项对象，封装删除条件 通用Mapper
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(specification.getId());
            specificationOptionMapper.delete(so);
            // 第二步：往规格选项表插入数据
            specificationOptionMapper.save(specification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    //删除品牌
    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            for (Serializable id : ids) {
                SpecificationOption so = new SpecificationOption();
                so.setSpecId((Long) id);
                specificationOptionMapper.delete(so);
                specificationMapper.deleteByPrimaryKey(id);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        /*
        这个只能删除specification表的数据
        //创建示范对象
        Example example = new Example(Specification.class);
        //创建条件对象
        Example.Criteria criteria = example.createCriteria();
        //添加in条件
        criteria.andIn("id", Arrays.asList(ids));
        //根据条件删除
        specificationMapper.deleteByExample(example);*/
    }

    @Override
    public Specification findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Specification> findAll() {
        return null;
    }

    /**
     * 分页查询列表
     */
    @Override
    public PageResult findByPage(Specification specification, int page, int rows) {
        //开始分页
        PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows)
                .doSelectPageInfo(new ISelect() {
                    @Override
                    public void doSelect() {
                        specificationMapper.findAll(specification);
                    }
                });
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据规格主键查询规格选项
     */
    @Override
    public List<SpecificationOption> findSpecOption(Long id) {
        try {
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(id);
            return specificationOptionMapper.select(so);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //查询所有的规格id与name
    @Override
    public List<Map<String, Object>> findAllByIdAndName() {

        try {
            return specificationMapper.findAllByIdAndName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
