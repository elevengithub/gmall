package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 品牌API
 */
@RequestMapping("/admin/product")
@RestController
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * 分页查询品牌列表
     * @param page  查询第几页
     * @param limit  每页查询多少条数据
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result pageBaseTrademark(@PathVariable("page")Long page,
                                    @PathVariable("limit")Long limit){
        Page<BaseTrademark> result = new Page<>(page,limit);
        return Result.ok(baseTrademarkService.page(result));
    }

    /**
     * 添加品牌
     * @param baseTrademark 品牌对象
     * @return
     */
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 根据id获取品牌
     * @param id 品牌id
     * @return
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable("id")Long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    /**
     * 修改品牌
     * @param baseTrademark  品牌对象
     * @return
     */
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    /**
     * 根据id删除品牌
     * @param id  品牌id
     * @return
     */
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result deleteBaseTrademark(@PathVariable("id")Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }
}
