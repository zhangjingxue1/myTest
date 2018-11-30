package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    /**
     * 引用服务接口代理对象
     * timeout:调用服务接口方法返回数据超时间的毫秒数
     */
    @Reference(timeout = 10000)
    private BrandService brandService;

    /**
     * 查询所有品牌
     */
    @GetMapping("/findBrandList")
    public List<Map<String, Object>> findBrandList() {
        return brandService.findAllByIdAndName();
    }

    //删除品牌
    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            brandService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //分页查询品牌
    //(换成post就不会又乱码问题,但是一般查询都用的get请求,post请求用来做修改添加那些)
    //用get就会又乱码问题,解决方法用转码问题解决
    @GetMapping("/findByPage")
    public PageResult findByPage(Brand brand, Integer page, Integer rows) {
        //get请求解决中文乱码
        //stringUtils.isNoneBlank工具类判断字符串是否为空有经验的程序员使用
        if (brand != null && StringUtils.isNoneBlank(brand.getName())) {
            try {
                brand.setName(new String(brand.getName().
                        getBytes("ISO8859-1"), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return brandService.findByPage(brand, page, rows);
    }

    //添加品牌
    //{name:xxx,first:xxx}这种请求的参数无法正常使用对象封装
    //利用@RequestBody注解自动解析根据对象的属性封装参数
    //也可以用Map<String,Object>封装
    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand) {
        try {
            brandService.save(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //修改品牌
    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //查询全部品牌
    @GetMapping("/findAll")
    public List<Brand> findAll() {
        return brandService.findAll();
    }
}
