package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.SolrItem;
import com.pinyougou.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
@Transactional
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    //搜索方法
    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        //创建Map集合封装返回数据
        HashMap<String, Object> data = new HashMap<>();
        //获取检索关键字
        String keywords = (String) params.get("keywords");

        /**分页*/
        //获取当前页码
        Integer page = (Integer) params.get("page");
        //第一次访问page是空
        if (page == null || page < 1) {
            //默认是第一页
            page = 1;
        }
        //获取每页显示的记录数据
        Integer rows = (Integer) params.get("rows");
        //第一次访问每页大小是空
        if (rows == null) {
            //默认是20个
            rows = 20;
        }

        //判断检索关键字
        if (StringUtils.isNoneBlank(keywords)) {
            /** 处理高亮查询 */
            //创建高亮查询对象完成高亮查询需求
            SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery();
            //创建高亮选项对象(这个对象作用只是封装高亮参数)
            HighlightOptions highlightOptions = new HighlightOptions();
            //设置高亮前缀
            highlightOptions.setSimplePrefix("<font color='blue'>");
            //设置高亮域(关键字)
            highlightOptions.addField("title");
            //设置高亮后缀
            highlightOptions.setSimplePostfix("</font>");
            //设置高亮选项
            highlightQuery.setHighlightOptions(highlightOptions);
            //创建查询条件
            Criteria criteria = new Criteria("keywords").is(keywords);
            //添加查询条件(关键字)
            highlightQuery.addCriteria(criteria);

            /**处理搜索过滤前提要有普通域的配置*/
            //按照商品分类过滤
            if (!"".equals(params.get("category"))) {
                Criteria criteria1 = new Criteria("category")
                        .is(params.get("category"));
                //添加过滤条件
                highlightQuery.addFilterQuery(new SimpleHighlightQuery(criteria1));
            }

            //按照品牌过滤
            if (!"".equals(params.get("brand"))) {
                Criteria criteria1 = new Criteria("brand")
                        .is(params.get("brand"));
                //添加过滤条件
                highlightQuery.addFilterQuery(new SimpleHighlightQuery(criteria1));
            }

            //按照规格过滤
            if (params.get("spec") != null) {
                Map<String, String> specMap = (Map) params.get("spec");
                for (String key : specMap.keySet()) {
                    Criteria criteria1 = new Criteria("spec_" + key).is(specMap.get(key));
                    //添加过滤条件
                    highlightQuery.addFilterQuery(new SimpleHighlightQuery(criteria1));
                }
            }

            //按照价格过滤
            if (!"".equals(params.get("price"))) {
                //得到价格范围数组0-500,1000-2000,3000-*
                String[] price = params.get("price").toString().split("-");
                //如果价格区间起点不等于0用greaterThanEqual大于等于多少
                if (!price[0].equals("0")) {
                    Criteria criteria1 = new Criteria("price")
                            .greaterThanEqual(price[0]);
                    //添加过滤条件
                    highlightQuery.addFilterQuery(new SimpleHighlightQuery(criteria1));
                }
                //如果价格区间重点不等于*号用lessThanEqual小于等于多少
                if (!price[1].equals("*")) {
                    Criteria criteria1 = new Criteria("price")
                            .lessThanEqual(price[1]);
                    //添加过滤条件
                    highlightQuery.addFilterQuery(new SimpleHighlightQuery(criteria1));
                }
            }

            /**处理排序*/
            String sortValue = (String) params.get("sort");//ASC DESC排序规则
            String sortFieId = (String) params.get("sortFieId");//排序域

            if (StringUtils.isNoneBlank(sortValue) &&
                    StringUtils.isNoneBlank(sortFieId)) {
                //第一个参数是升序还是降序,参数二排序域
                Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ?
                        Sort.Direction.ASC : Sort.Direction.DESC, sortFieId);
                //增加排序
                highlightQuery.addSort(sort);
            }


            /**处理分页查询数*/
            //设置起始记录查询数
            highlightQuery.setOffset((page - 1) * rows);
            //设置每页显示的记录数
            highlightQuery.setRows(rows);

            /**查询solr索引库*/
            //分页查询,得到高亮分页查询对象
            HighlightPage<SolrItem> highlightPage = solrTemplate.
                    queryForHighlightPage(highlightQuery, SolrItem.class);
            //循环高亮项集合
            for (HighlightEntry<SolrItem> he : highlightPage.getHighlighted()) {
                //获取检索到的原始体
                SolrItem solrItem = he.getEntity();
                //判断高亮集合及集合中的第一个Field高亮内容
                if (he.getHighlights().size() > 0
                        && he.getHighlights().get(0).
                        getSnipplets().size() > 0) {
                    //设置高亮结果
                    solrItem.setTitle(he.getHighlights().get(0).
                            getSnipplets().get(0));
                }
            }
            /**封装数据*/
            //获取内容
            data.put("rows", highlightPage.getContent());
            //设置总页数
            data.put("totalPages", highlightPage.getTotalPages());
            //设置总记录数
            data.put("total", highlightPage.getTotalElements());
        } else {
            /**简单查询*/
            //创建查询对象
            SimpleQuery query = new SimpleQuery("*:*");
            /**处理分页查询数*/
            //设置起始记录查询数
            query.setOffset((page - 1) * rows);
            //设置每页显示的记录数
            query.setRows(rows);
            /**查询solr索引库*/
            //分页搜索
            ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
            /**封装数据*/
            //获取内容
            data.put("rows", scoredPage.getContent());
            //设置总页数
            data.put("totalPages", scoredPage.getTotalPages());
            //设置总记录数
            data.put("total", scoredPage.getTotalElements());
        }
        /**返回数据*/
        return data;
    }

    /**
     * 添加或修改商品索引
     */
    @Override
    public void saveOrUpdate(List<SolrItem> solrItems) {
        //更新商品solr索引库
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        //如果存入索引库没出现异常就提交没异常就是0
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            //否则回滚
            solrTemplate.rollback();
        }
    }

    /**
     * 删除商品索引
     */
    @Override
    public void delete(List<Long> goodsIds) {
        //创建solr的条件对象
        SimpleQuery query = new SimpleQuery();
        //加入条件
        Criteria criteria = new Criteria("goodsId").in(Arrays.asList(goodsIds));
        query.addCriteria(criteria);
        //调用solrTemplate的删除方法把条件给他
        UpdateResponse updateResponse = solrTemplate.delete(query);
        //返回的状态码是0删除成功提交否则回滚
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }
}
