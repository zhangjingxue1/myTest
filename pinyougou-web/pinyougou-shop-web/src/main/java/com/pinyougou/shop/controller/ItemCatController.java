package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    @Reference(timeout = 10000)
    private ItemCatService itemCatService;

    //根据父id查询分类列表
    @GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(
            @RequestParam(value = "parentId",defaultValue = "0") Long parentId) {
       return itemCatService.findItemCatByParentId(parentId);
    }
}
