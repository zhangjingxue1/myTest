package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ItemSearchController {

    @Reference(timeout = 30000)
    private ItemSearchService itemSearchService;

    /**
     * 搜索方法有条件的
     */
    @PostMapping("/Search")
    public Map<String, Object> search(
            @RequestBody Map<String, Object> params) {
        return itemSearchService.search(params);
    }

    /**
     * 搜索方法无条件的
     */
    @PostMapping("/noSearch")
    public Map<String, Object> noSearch() {
        HashMap<String, Object> params = new HashMap<>();
        return itemSearchService.search(params);
    }
}
