package com.pinyougou.service;

import com.pinyougou.pojo.Provinces;
import java.util.List;
import java.io.Serializable;
/**
 * ProvincesService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface ProvincesService {

	/** 添加方法 */
	void save(Provinces provinces);

	/** 修改方法 */
	void update(Provinces provinces);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Provinces findOne(Serializable id);

	/** 查询全部 */
	List<Provinces> findAll();

	/** 多条件分页查询 */
	List<Provinces> findByPage(Provinces provinces, int page, int rows);

	/**根据省份id查询省份*/
    List<Provinces> findAddressByPId(Long provinceId);
	/**查询所有省份*/
    List<Provinces> findAddressProvince();
	/**根据provinceId查询对应的省*/
    Provinces findProvinceByPID(String provinceId);
}