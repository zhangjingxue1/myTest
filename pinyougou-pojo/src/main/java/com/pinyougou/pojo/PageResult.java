package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.List;

//分页结果实体
public class PageResult implements Serializable {
    //总记录数
    private long total;
    //分页数据集合里面定义?占位符,代表未确定参数,由传入的时候指定
    private List<?> rows;

    public PageResult() {
    }

    public PageResult(long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
