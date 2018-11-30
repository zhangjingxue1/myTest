package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.AreasService;
import com.pinyougou.service.CitiesService;
import com.pinyougou.service.ProvincesService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference(timeout = 10000)
    private AddressService addressService;
    @Reference(timeout = 10000)
    private ProvincesService provincesService;
    @Reference(timeout = 10000)
    private CitiesService citiesService;
    @Reference(timeout = 10000)
    private AreasService areasService;

    /**
     * 查询地址
     */
    @GetMapping("/findUserAddress")
    public List<Address> findUserAddress(HttpServletRequest request) {
        try {
            String userId = request.getRemoteUser();
            List<Address> addressList = addressService.findAddressByUser(userId);
            for (Address address : addressList) {
                if (address.getProvinceId() != null) {
                    address.setProvince(findProvinceToPID(address.getProvinceId()).getProvince());
                }
                if (address.getCityId() != null) {
                    address.setCity(findCityToCID(address.getCityId()).getCity());
                }
                if (address.getTownId() != null) {
                    address.setTown(findAreasToTID(address.getTownId()).getArea());
                }
            }
            return addressList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有省份
     */
    @GetMapping("/findAddressProvince")
    public List<Provinces> findAddressProvince() {
        try {
            return provincesService.findAddressProvince();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据省份id查询市
     */
    @GetMapping("/findCitiesByPID")
    public List<Cities> findCitiesByPID(
            @RequestParam(value = "provinceId") Long provinceId) {
        try {
            return citiesService.findAddressByPId(provinceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据省份id查询市
     */
    @GetMapping("/findAreaByCID")
    public List<Areas> findAreaByCID(
            @RequestParam(value = "cityId") Long cityId) {
        try {
            return areasService.findAreaByCID(cityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加方法
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Address address, HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            address.setUserId(username);
            address.setIsDefault("0");
            address.setCreateDate(new Date());
            addressService.save(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除地址
     */
    @GetMapping("/delete")
    public boolean delete(Long id) {
        try {
            addressService.delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改方法
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Address address) {
        try {
            addressService.update(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改为默认
     */
    @GetMapping("/defaultAddress")
    public boolean defaultAddress(String id, HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            return addressService.defaultAddress(id, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据省份id查询对应的省
     */
    private Provinces findProvinceToPID(String provinceId) {
        try {
            return provincesService.findProvinceByPID(provinceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据城市id查询城市
     */
    private Cities findCityToCID(String cityId) {
        try {
            return citiesService.findCityByCID(cityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过县级id查询县级
     */
    private Areas findAreasToTID(String areaId) {
        try {
            return areasService.findAreasByTID(areaId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
