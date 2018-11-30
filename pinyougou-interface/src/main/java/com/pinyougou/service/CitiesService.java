package com.pinyougou.service;

import com.pinyougou.pojo.Cities;

import java.io.Serializable;
import java.util.List;
/**
 * CitiesService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface CitiesService {

	/** 添加方法 */
	void save(Cities cities);

	/** 修改方法 */
	void update(Cities cities);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Cities findOne(Serializable id);

	/** 查询全部 */
	List<Cities> findAll();

	/** 多条件分页查询 */
	List<Cities> findByPage(Cities cities, int page, int rows);
	/**根据省份id查询市*/
    List<Cities> findAddressByPId(Long provinceId);
	/**
	 * 根据城市id查询城市
	 */
    Cities findCityByCID(String cityId);
}