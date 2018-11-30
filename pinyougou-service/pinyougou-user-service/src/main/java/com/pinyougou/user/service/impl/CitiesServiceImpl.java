package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.pojo.Cities;
import com.pinyougou.service.CitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.CitiesService")
@Transactional
public class CitiesServiceImpl implements CitiesService {
    @Autowired
    private CitiesMapper citiesMapper;

    /**
     * 根据省份id查询市
     */
    @Override
    public List<Cities> findAddressByPId(Long provinceId) {
        try {
            Cities cities = new Cities();
            cities.setProvinceId(provinceId + "");
            return citiesMapper.select(cities);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据城市id查询城市
     */
    @Override
    public Cities findCityByCID(String cityId) {

        try {
            return citiesMapper.findCityByCID(cityId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Cities cities) {

    }

    @Override
    public void update(Cities cities) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Cities findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Cities> findAll() {
        return null;
    }

    @Override
    public List<Cities> findByPage(Cities cities, int page, int rows) {
        return null;
    }

}
