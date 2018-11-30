package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AreasMapper;
import com.pinyougou.pojo.Areas;
import com.pinyougou.service.AreasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
@Service(interfaceName = "com.pinyougou.service.AreasService")
@Transactional
public class AreasServiceImpl implements AreasService {
    @Autowired
    private AreasMapper areasMapper;

    /**根据cityId查询市区域*/
    @Override
    public List<Areas> findAreaByCID(Long cityId) {
        Areas areas = new Areas();
        areas.setCityId(cityId+"");
        return areasMapper.select(areas);
    }
    /**通过县级id查询县级 */
    @Override
    public Areas findAreasByTID(String areaId) {

        return areasMapper.findAreasByTID(areaId);
    }

    @Override
    public void save(Areas areas) {

    }

    @Override
    public void update(Areas areas) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Areas findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Areas> findAll() {
        return null;
    }

    @Override
    public List<Areas> findByPage(Areas areas, int page, int rows) {
        return null;
    }


}
