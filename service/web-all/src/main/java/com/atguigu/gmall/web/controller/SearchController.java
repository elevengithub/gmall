package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    @GetMapping("/list.html")
    public String search(SearchParamVo searchParam, Model model){

        Result<SearchResponseVo> result = searchFeignClient.search(searchParam);
        SearchResponseVo responseVo = result.getData();

        model.addAttribute("searchParam",responseVo.getSearchParam());
        model.addAttribute("propsParamList",responseVo.getPropsParamList());
        model.addAttribute("trademarkList",responseVo.getTrademarkList());
        model.addAttribute("urlParam",responseVo.getUrlParam());
        model.addAttribute("attrsList",responseVo.getAttrsList());
        model.addAttribute("orderMap",responseVo.getOrderMap());
        model.addAttribute("goodsList",responseVo.getGoodsList());
        model.addAttribute("pageNo",responseVo.getPageNo());
        model.addAttribute("totalPages",responseVo.getTotalPages());

        return "list/index";
    }
}
