package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference(timeout = 10000)
    private SpecificationService specificationService;

    /**
     * 查询所有的规格id与name
     */
    @GetMapping("/findSpecList")
    public List<Map<String, Object>> findSpecList() {
        return specificationService.findAllByIdAndName();
    }

    /**
     * 修改规格
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Specification specification) {
        try {
            specificationService.update(specification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //多条件分页查询规格
    @GetMapping("/findByPage")
    public PageResult findByPage(Specification specification,
                                 Integer page, Integer rows) {
        if (specification != null &&
                StringUtils.isNoneBlank(specification.getSpecName())) {
            try {
                specification.setSpecName(new String(specification.getSpecName().
                        getBytes("ISO8859-1"), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return specificationService.findByPage(specification, page, rows);
    }

    /**
     * 添加规格
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Specification specification) {
        try {
            specificationService.save(specification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* 删除规格*/
    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            specificationService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据规格主键查询规格选项
     */
    @GetMapping("/findSpecOption")
    public List<SpecificationOption> findSpecOption(Long id) {
        return specificationService.findSpecOption(id);
    }

}
