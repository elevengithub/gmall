package com.atguigu.gmall.search.service.impl;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.vo.search.SearchConst;
import com.google.common.collect.Lists;
import com.atguigu.gmall.model.vo.search.OrderMapVo;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    ElasticsearchRestTemplate restTemplate;

    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void deleteGoods(Long id) {
        goodsRepository.deleteById(id);
    }

    @Override
    public SearchResponseVo search(SearchParamVo searchParam) {
        //1、构建查询的Dsl语句
        Query query = buildQueryDsl(searchParam);
        //2、获取查询结果
        SearchHits<Goods> searchHits = restTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));
        //3、封装响应数据
        SearchResponseVo responseVo = buildResponseVo(searchHits,searchParam);
        return responseVo;
    }

    /**
     * 封装响应数据SearchResponseVo
     * @param searchHits  根据DSL语句查询的结果
     * @param searchParam  搜索条件
     * @return
     */
    private SearchResponseVo buildResponseVo(SearchHits<Goods> searchHits, SearchParamVo searchParam) {
        SearchResponseVo responseVo = new SearchResponseVo();
        //1、设置搜索参数
        responseVo.setSearchParam(searchParam);
        //2、设置品牌面包屑
        if (!StringUtils.isEmpty(searchParam.getTrademark())) {
            responseVo.setTrademarkParam(searchParam.getTrademark());
        }
        //3、设置属性面包屑
        List<SearchAttr> list = new ArrayList<>();
        if (searchParam.getProps() != null && searchParam.getProps().length > 0) {
            SearchAttr searchAttr = new SearchAttr();
            for (String prop : searchParam.getProps()) {
                searchAttr.setAttrId(Long.parseLong(prop.split(":")[0]));
                searchAttr.setAttrValue(prop.split(":")[1]);
                searchAttr.setAttrName(prop.split(":")[2]);
                list.add(searchAttr);
            }
            responseVo.setPropsParamList(list);
        }
        //TODO 4、设置品牌集合，需要聚合分析
        responseVo.setTrademarkList(Lists.newArrayList());
        //TODO 5、设置属性集合，需要聚合分析
        responseVo.setAttrsList(Lists.newArrayList());
        //6、设置排序
        OrderMapVo orderMapVo = new OrderMapVo();
        orderMapVo.setSort(searchParam.getOrder().split(":")[0]);
        orderMapVo.setType(searchParam.getOrder().split(":")[1]);
        responseVo.setOrderMap(orderMapVo);
        //7、设置商品集合
        List<Goods> goodsList = new ArrayList<>();
        searchHits.getSearchHits().forEach(hits -> {
            Goods goods = hits.getContent();
            goodsList.add(goods);
        });
        responseVo.setGoodsList(goodsList);
        //8、设置分页信息
        responseVo.setPageNo(searchParam.getPageNo());
        //9、设置总页数
        long totlePages = (searchHits.getTotalHits() % SearchConst.SEARCH_PAGE_SIZE) == 0 ?
                (searchHits.getTotalHits() % SearchConst.SEARCH_PAGE_SIZE) :
                (searchHits.getTotalHits() % SearchConst.SEARCH_PAGE_SIZE) + 1;
        responseVo.setTotalPages(new Integer(totlePages + ""));
        //10、设置url参数
        responseVo.setUrlParam(buildUrlParam(searchParam));

        return responseVo;
    }

    /**
     * 创建搜索时的url
     * @param searchParam  搜索参数
     * @return
     */
    private String buildUrlParam(SearchParamVo searchParam) {
        StringBuilder stringBuilder = new StringBuilder("list.html?");
        if (searchParam.getCategory1Id() != null) {
            stringBuilder.append("&category1Id=" + searchParam.getCategory1Id());
        }
        if (searchParam.getCategory2Id() != null) {
            stringBuilder.append("&category2Id=" + searchParam.getCategory2Id());
        }
        if (searchParam.getCategory3Id() != null) {
            stringBuilder.append("&category3Id=" + searchParam.getCategory3Id());
        }
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            stringBuilder.append("&keyword=" + searchParam.getKeyword());
        }
        if (!StringUtils.isEmpty(searchParam.getTrademark())) {
            stringBuilder.append("&trademark=" + searchParam.getTrademark());
        }
        if (searchParam.getProps() != null && searchParam.getProps().length > 0) {
            for (String prop : searchParam.getProps()) {
                stringBuilder.append("&props=" + prop);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 根据查询条件构建DSL查询语句
     * @param searchParam  查询条件
     * @return
     */
    private Query buildQueryDsl(SearchParamVo searchParam) {
        //1、创建一个boolQuery
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (searchParam.getCategory3Id() != null) {
            queryBuilder.must(QueryBuilders.termQuery("category3Id",searchParam.getCategory3Id()));
        }
        if (searchParam.getCategory2Id() != null) {
            queryBuilder.must(QueryBuilders.termQuery("category2Id",searchParam.getCategory2Id()));
        }
        if (searchParam.getCategory1Id() != null) {
            queryBuilder.must(QueryBuilders.termQuery("category1Id",searchParam.getCategory1Id()));
        }
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            queryBuilder.must(QueryBuilders.matchQuery("title",searchParam.getKeyword()));
        }
        //trademark=1:小米
        if (!StringUtils.isEmpty(searchParam.getTrademark())) {
            String s = searchParam.getTrademark().split(":")[0];
            queryBuilder.must(QueryBuilders.termQuery("tmId",s));
        }
        //props=24:256G:机身内存&props=23:8G:运行内存
        if (searchParam.getProps() != null && searchParam.getProps().length > 0) {
            for (String prop : searchParam.getProps()) {
                String attrId = prop.split(":")[0];
                String attrValue = prop.split(":")[1];

                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQuery.must(QueryBuilders.termQuery("attrs.attrValue",attrValue));
                NestedQueryBuilder nestedQuery = new NestedQueryBuilder("attrs",boolQuery, ScoreMode.None);

                queryBuilder.must(nestedQuery);
            }
        }

        //创建一个原生的检索条件
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);

        //设置排序
        //综合排序：order=1:desc   价格排序：order=2:asc
        if (!StringUtils.isEmpty(searchParam.getOrder())) {
            String orderField = "hotScore";
            switch (searchParam.getOrder().split(":")[0]){
                case "1": orderField = "hotScore"; break;
                case "2": orderField = "price"; break;
                default: orderField = "hotScore";
            }
            Sort sort = Sort.by(orderField);
            if ("asc".equals(searchParam.getOrder().split(":")[1])) {
                sort = sort.ascending();
            } else {
                sort = sort.descending();
            }
            query.addSort(sort);
        }

        //设置分页
        PageRequest pageRequest = PageRequest.of(searchParam.getPageNo() - 1, SearchConst.SEARCH_PAGE_SIZE);
        query.setPageable(pageRequest);

        //设置搜索关键字高亮显示
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            //创建高亮构建器
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置高亮属性和前后缀
            highlightBuilder.field("title").preTags("<span style='color:red'>").postTags("</span>");
            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
            query.setHighlightQuery(highlightQuery);
        }

        //TODO 聚合分析根据上述条件检索到的商品涉及到的所有品牌和所有平台属性


        return query;
    }
}
