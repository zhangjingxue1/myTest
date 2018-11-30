package com.pinyougou.service;

import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.PageResult;

import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * BrandService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface BrandService {
	/** 查询全部 */
	List<Brand> findAll();

    /** 添加方法 */
    void save(Brand brand);

    /**修改*/
    void update(Brand brand);
    /**分页查询*/
    PageResult findByPage(Brand brand, int page, int rows);
    /**删除品牌*/
    void deleteAll(Serializable[] ids);
    /**查询所有的品牌id与name*/
    List<Map<String,Object>> findAllByIdAndName();

}