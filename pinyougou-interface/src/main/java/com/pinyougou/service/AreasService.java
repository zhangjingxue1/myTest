package com.pinyougou.service;

import com.pinyougou.pojo.Areas;
import java.util.List;
import java.io.Serializable;
/**
 * AreasService 服务接口
 * @date 2018-09-29 09:53:20
 * @version 1.0
 */
public interface AreasService {

	/** 添加方法 */
	void save(Areas areas);

	/** 修改方法 */
	void update(Areas areas);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Areas findOne(Serializable id);

	/** 查询全部 */
	List<Areas> findAll();

	/** 多条件分页查询 */
	List<Areas> findByPage(Areas areas, int page, int rows);
	/**根据cityId查询市区域*/
	List<Areas> findAreaByCID(Long cityId);

	/**通过县级id查询县级 */
	Areas findAreasByTID(String areaId);
}