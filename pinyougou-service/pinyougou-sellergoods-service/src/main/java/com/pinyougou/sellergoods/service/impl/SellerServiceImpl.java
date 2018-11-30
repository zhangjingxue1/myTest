package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SellerMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SellerService")
@Transactional
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerMapper sellerMapper;

    /**
     * 商家注册
     */
    @Override
    public void save(Seller seller) {
        try {
            seller.setStatus("0");
            seller.setCreateTime(new Date());
            sellerMapper.insertSelective(seller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**商家修改资料*/
    @Override
    public void update(Seller seller) {
        try {
            sellerMapper.updateByPrimaryKeySelective(seller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Seller findOne(Serializable id) {
        return sellerMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Seller> findAll() {
        return null;
    }

    /**
     * 多条件查询商家列表
     */
    @Override
    public PageResult findByPage(Seller seller, int page, int rows) {
        try {
            PageInfo pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    sellerMapper.findByPage(seller);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 审核方法,通过设置为1,不通过设置为2,关闭有问题的商家设置为3
     */
    @Override
    public void checkPass(Seller seller, Integer status) {
        try {
            seller.setStatus(status + "");
            sellerMapper.checkPass(seller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用通用mapper操作审核方法以及多条件查询商家列表
     */

   /* public PageResult mapperFindByPage(Seller seller, int page, int rows) {
        try {
            PageInfo pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    //创建示范对象
                    Example example = new Example(Seller.class);
                    //创建条件对象
                    Example.Criteria criteria = example.createCriteria();
                    //添加多条件查询的条件
                    if (seller != null && !StringUtil.isEmpty(seller.getStatus())) {
                        //相当于sql语句的status=? ,第一个参数给表对应的成员属性,第二个参数给对象属性
                        criteria.andEqualTo("status",seller.getStatus());
                    }
                    //添加多条件查询的条件
                    if (seller != null && !StringUtil.isEmpty(seller.getName())) {
                        //相当于sql语句的name like %?% ,
                        // 第一个参数给表对应的成员属性,第二个参数给对象属性
                        criteria.andLike("name","%"+seller.getName()+"%");
                    }
                    //添加多条件查询的条件
                    if (seller != null && !StringUtil.isEmpty(seller.getNickName())) {
                        //相当于sql语句的nick_name like %?% ,
                        // 第一个参数给表对应的成员属性,第二个参数给对象属性
                        criteria.andLike("name","%"+seller.getNickName()+"%");
                    }
                    //根据模版查询
                    sellerMapper.selectByExample(example);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void mapperUpdateStatus(String sellerId,String status){
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        //选择性修改只对有值的进行修改判断
        sellerMapper.updateByPrimaryKeySelective(seller);
    }*/

}
