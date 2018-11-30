package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {
    //注入数据访问接口代理对象
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    //注入模版id查询所有的规格跟规格选项
    @Override
    public List<Map> findSpecByTemplateId(Long id) {
        try {
            //根据主键id查询模版,获取类型模版对象
            TypeTemplate typeTemplate = findOne(id);
            /**
             * 模版表查询出来的spec_ids
             * [{"id":33,"text":"电视屏幕尺寸"},{"id":35,"text":"分辨率"}...]
             * 获取模版中所有的规格，转化成  List<Map>
             *     把json字符串转化为List<Map>集合用JSON.parseArray();
             *
             *     参数一,要解析的json字符串
             *     参数二,大括号需要转化的东西,
             *         可以转化为map.实体类(实体类要加上字符串的字段成员属性比如id.text等)
             */
            List<Map> specLists = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
            //迭代模版中的所有规格
            for (Map map : specLists) {
                //创建查询条件对象
                SpecificationOption so = new SpecificationOption();
                so.setSpecId(Long.valueOf(map.get("id").toString()));
                //通过规格id,查询规格选项数据
                //select * from tb_specification_option where spec_id = 27
                List<SpecificationOption> specOptions = specificationOptionMapper.select(so);
                // {"id":27,"text":"网络", "options" : [{},{}]}
                // 往map对象中添加一个key
                map.put("options", specOptions);
            }
            return specLists;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加类型模版
     */
    @Override
    public void save(TypeTemplate typeTemplate) {
        //选择性添加,会判断对象的属性是否有值,有值就生成到insert语句中
        //免去麻烦的sql语句跟数据的封装
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    /**
     * 修改类型模版
     */
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Serializable id) {


    }

    @Override
    public void deleteAll(Serializable[] ids) {
        //创建示范对象
        Example example = new Example(TypeTemplate.class);
        //创建条件对象
        Example.Criteria criteria = example.createCriteria();
        //添加in条件
        criteria.andIn("id", Arrays.asList(ids));
        //条件删除
        typeTemplateMapper.deleteByExample(example);
    }

    /**
     * 根据模版id查询品牌brandIds
     */
    @Override
    public TypeTemplate findOne(Serializable id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    /**
     * 分页查询列表
     */
    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(() ->
                typeTemplateMapper.findAll(typeTemplate));
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


}
