package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ProvincesMapper;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.ProvincesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.ProvincesService")
@Transactional
public class ProvincesServiceImpl implements ProvincesService {
    @Autowired
    private ProvincesMapper provincesMapper;

    @Override
    public void save(Provinces provinces) {

    }

    @Override
    public void update(Provinces provinces) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Provinces findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Provinces> findAll() {
        return null;
    }

    @Override
    public List<Provinces> findByPage(Provinces provinces, int page, int rows) {
        return null;
    }

    /**
     * 根据省份id查询省
     */
    @Override
    public List<Provinces> findAddressByPId(Long provinceId) {
        Provinces provinces = new Provinces();
        provinces.setProvinceId(provinceId + "");
        return provincesMapper.select(provinces);
    }

    /**
     * 查询所有省份
     */
    @Override
    public List<Provinces> findAddressProvince() {
        try {
            return provincesMapper.selectAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**根据省id查询对应的省*/
    @Override
    public Provinces findProvinceByPID(String provinceId) {
        return provincesMapper.findProvinceByPID(provinceId);
    }
}
