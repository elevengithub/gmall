package com.atguigu.gmall.search.service.impl;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.vo.search.*;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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
import java.util.stream.Collectors;

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
     * 更新商品热度分
     * @param skuId
     * @param hotScore
     */
    @Override
    public void updateHotScore(Long skuId, Long hotScore) {
        //1、根据id获取es中的商品
        Goods goods = goodsRepository.findById(skuId).get();
        //2、设置商品的热度分
        goods.setHotScore(hotScore);
        //3、重新保存商品
        goodsRepository.save(goods);
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
        //4、设置品牌集合，需要聚合分析
        List<TrademarkVo> trademarkList = buildTrademarkList(searchHits);
        responseVo.setTrademarkList(trademarkList);
        //5、设置属性集合，需要聚合分析
        List<AttrVo> attrsList = buildAttrsList(searchHits);
        responseVo.setAttrsList(attrsList);
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
     * 构建List<AttrVo>
     * @param searchHits
     * @return
     */
    private List<AttrVo> buildAttrsList(SearchHits<Goods> searchHits) {
        //1、获取嵌入式聚合分析attrsAgg
        ParsedNested attrsAgg = searchHits.getAggregations().get("attrsAgg");
        //2、获取桶内的
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
        List<AttrVo> attrVos = attrIdAgg.getBuckets().stream().map(attr -> {
            AttrVo attrVo = new AttrVo();
            //获取attrIdAgg桶中的key设置attrVo的id
            attrVo.setAttrId((Long) attr.getKey());
            //获取attrNameAgg桶中的key设置为attrVo的attrName
            ParsedStringTerms attrNameAgg = attr.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //获取attrValueAgg桶中的所有key组成的集合设置为attrVo的attrValueList
            ParsedStringTerms attrValueAgg = attr.getAggregations().get("attrValueAgg");
            List<String> attrValueList = attrValueAgg
                    .getBuckets()
                    .stream()
                    .map(attrValue -> attrValue.getKeyAsString())
                    .collect(Collectors.toList());
            attrVo.setAttrValueList(attrValueList);
            return attrVo;
        }).collect(Collectors.toList());
        return attrVos;
    }

    /**
     * 构建TrademarkList
     * @param searchHits  es搜索结果
     * @return
     */
    private List<TrademarkVo> buildTrademarkList(SearchHits<Goods> searchHits) {
        //1、获取聚合分析tmIdAgg的结果
        ParsedLongTerms tmIdAgg = searchHits.getAggregations().get("tmIdAgg");
        //2、获取所有TrademarkVo，存入List<TrademarkVo>
        List<TrademarkVo> trademarkVos = tmIdAgg.getBuckets().stream().map(tm -> {
            TrademarkVo trademarkVo = new TrademarkVo();
            //根据buckets的key获取tmId
            trademarkVo.setTmId((Long) tm.getKey());
            //获取子聚合tmNameAgg的buckets的key，设置TrademarkVo的tmName属性
            ParsedStringTerms tmNameAgg = tm.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmName(tmName);
            //获取子聚合tmLogoUrlAgg的buckets的key，设置TrademarkVo的tmName属性
            ParsedStringTerms tmLogoUrlAgg = tm.getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmLogoUrl(tmLogoUrl);
            return trademarkVo;
        }).collect(Collectors.toList());
        return trademarkVos;
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

        //2、构建三级分类查询语句
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
        //3、构建品牌查询语句  trademark=1:小米
        if (!StringUtils.isEmpty(searchParam.getTrademark())) {
            String s = searchParam.getTrademark().split(":")[0];
            queryBuilder.must(QueryBuilders.termQuery("tmId",s));
        }
        //4、构建属性查询语句  props=24:256G:机身内存&props=23:8G:运行内存
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

        //5、创建一个原生的检索条件
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);

        //6、设置排序
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

        //7、设置分页
        PageRequest pageRequest = PageRequest.of(searchParam.getPageNo() - 1, SearchConst.SEARCH_PAGE_SIZE);
        query.setPageable(pageRequest);

        //8、设置搜索关键字高亮显示
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            //创建高亮构建器
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置高亮属性和前后缀
            highlightBuilder.field("title").preTags("<span style='color:red'>").postTags("</span>");
            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
            query.setHighlightQuery(highlightQuery);
        }

        //9、根据上述条件检索到的商品聚合分析涉及到的所有品牌和所有平台属性
        //品牌聚合分析
        //1、构建聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId").size(1000);
        //2、构建子聚合tmNameAgg
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg").field("tmName").size(1);
        tmIdAgg.subAggregation(tmNameAgg);
        //3、构建子聚合
        TermsAggregationBuilder tmLogoUrlAgg = AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl").size(1);
        tmIdAgg.subAggregation(tmLogoUrlAgg);
        query.addAggregation(tmIdAgg);

        //属性聚合分析
        NestedAggregationBuilder nestedAgg = AggregationBuilders.nested("attrsAgg", "attrs");
        //1、构建attrIdAgg聚合分析
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);
        nestedAgg.subAggregation(attrIdAgg);
        //2、构建子聚合attrNameAgg
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1);
        attrIdAgg.subAggregation(attrNameAgg);
        //3、构建子聚合
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100);
        attrIdAgg.subAggregation(attrValueAgg);
        query.addAggregation(nestedAgg);

        return query;
    }
}
