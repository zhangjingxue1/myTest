package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.Content;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.ContentService;

import java.util.List;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;

/**
 * ContentServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2018-08-14 00:23:07
 */
@Service(interfaceName = "com.pinyougou.service.ContentService")
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加方法
     */
    public void save(Content content) {
        try {
            contentMapper.insertSelective(content);
            //增删改过后都要清除redis的key重置数据
            redisTemplate.delete("content");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     */
    public void update(Content content) {
        try {
            contentMapper.updateByPrimaryKeySelective(content);
            //增删改过后都要清除redis的key重置数据
            redisTemplate.delete("content");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            contentMapper.deleteByPrimaryKey(id);
            //增删改过后都要清除redis的key重置数据
            redisTemplate.delete("content");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Serializable[] ids) {
        try {
            // 创建示范对象
            Example example = new Example(Content.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            contentMapper.deleteByExample(example);
            //增删改过后都要清除redis的key重置数据
            redisTemplate.delete("content");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public Content findOne(Serializable id) {
        try {
            return contentMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Content> findAll() {
        try {
            return contentMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public PageResult findByPage(Content content, int page, int rows) {
        try {
            PageInfo<Content> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            contentMapper.selectAll();
                        }
                    });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据广告id查询广告列表,
     * 因为多人访问首页所以首页图片使用redis技术,
     * 看老师的这里要不断的try跟catch不明其原因
     * try catch的原因是怕服务器down掉了出现异常
     */
    @Override
    public List<Content> findContentByCId(Long contentId) {
        //初始化广告列表对象
        List<Content> contentList = null;
        try {
            /**从redis数据库中获取到数据内容*/
            //通过redis模版调用boundValueOps给他对应的key获取json数据,到时候数据会反序列化回来
            contentList = (List<Content>) redisTemplate.boundValueOps("content").get();
            //如果有数据
            if (contentList != null && contentList.size() > 0) {
                //直接返回数据给前端免去查询数据库
                return contentList;
            } else {
                //否则是第一次获取数据,key没有找到,查询数据库
                contentList = contentMapper.findContentByCId(contentId);
                //设置对应的key跟对应的值
                //boundValueOps这里不仅仅可以放字符串,还可以放对象(序列化数据存入,二进制数据的字符串)
                //try catch是因为怕存入数据时候redis服务器down了
                try {
                    redisTemplate.boundValueOps("content").set(contentList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //返回数据给前端
                return contentList;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 根据分类id查询广告内容 老师的代码 */
    /*public List<Content> findContentByCategoryId(Long categoryId){
     *//** 定义广告数据 *//*
        List<Content> contentList = null;
        try {
            *//** 从Redis中获取广告 *//*
            contentList = (List<Content>) redisTemplate
                    .boundValueOps("content").get();
            if (contentList != null && contentList.size() > 0) {
                return contentList;
            }
        }catch (Exception ex){}
        try{
            // 创建示范对象
            Example example = new Example(Content.class);
            // 创建查询条件对象
            Example.Criteria criteria = example.createCriteria();
            // 添加等于条件 category_id = categoryId
            criteria.andEqualTo("categoryId", categoryId);
            // 添加等于条件 status = 1
            criteria.andEqualTo("status", "1");
            // 排序(升序) order by sort_order asc
            example.orderBy("sortOrder").asc();

            *//** 查询广告数据 *//*
            contentList = contentMapper.selectByExample(example);
            try {
                *//** 存入Redis缓存 *//*
                redisTemplate.boundValueOps("content").set(contentList);
            }catch (Exception ex){}
            return contentList;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }*/


}