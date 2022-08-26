package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/admin/product")
@RestController
public class FileUploadController {

    @Autowired
    FileUploadService fileUploadService;

    /**
     * 文件上传
     * @param file 文件
     * @return
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart("file") MultipartFile file) throws Exception{
        String url = fileUploadService.upload(file);
        return Result.ok(url);
    }

    /**
     *测试前端传递不同参数后端接收方式
     * <form action="http://localhost:9000/admin/product/reg"
     *    enctype="multipart/form-data" method="post">
     *    头像：<input type="file" name="header" multiple/> <br/>
     *    生活照：<input type="file" name="shz"/> <br/>
     *    身份证：<input type="file" name="sfz"/> <br/>
     *    用户名：<input name="username"/> <br/>
     *    密码：<input name="password"/><br/>
     *    邮箱：<input name="email"/><br/>
     *    爱好： 篮球<input name="ah" type="checkbox" value="篮球"/>
     *    足球<input name="ah" type="checkbox" value="足球"/>
     *    <button>注册</button>
     * </form>
     * 各种注解接不同位置的请求数据
     * @RequestPart / @RequestPart[]   接请求参数里面的 单个文件项/文件数组
     * @RequestParam   无论是什么请求 接请求参数。 可以直接用一个pojo接收
     * @RequestHeader  获取浏览器发送请求中请求头中的某些信息
     * @PathVariable   接收请求路径上的占位符传参
     * @CookieValue    获取浏览器发送请求携带的cookie值
     */
    @PostMapping("/reg/{variable}")
    public Result paramTest(@RequestPart("header") MultipartFile[] header,
                            @RequestPart("shz") MultipartFile shz,
                            @RequestPart("sfz") MultipartFile sfz,
                            @RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam("email") String email,
                            @RequestParam("ah") String[] ah,
                            @RequestHeader("Cache-Control") String cache,
                            @PathVariable("variable") String variable,
                            @CookieValue("jsessionid") String jsessionid){
        Map<String,Object> map = new HashMap<>();
        map.put("headerSize",header.length);
        map.put("shz",shz.getSize());
        map.put("sfz",sfz.getSize());
        map.put("username",username);
        map.put("password",password);
        map.put("email",email);
        map.put("ah", Arrays.asList(ah));
        map.put("cache",cache);
        map.put("variable",variable);
        return Result.ok(map);
    }
}
