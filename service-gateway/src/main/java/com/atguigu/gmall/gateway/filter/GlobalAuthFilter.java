package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.GlobalUrlProperties;
import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalAuthFilter implements GlobalFilter {

    AntPathMatcher pathMatcher = new AntPathMatcher();
    @Autowired
    GlobalUrlProperties globalUrlProperties;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1、获取请求的uri和path路径
        String uri = exchange.getRequest().getURI().toString();
        String path = exchange.getRequest().getPath().toString();
        //2、判断path路径是否是 /img/** /css/** /js/**等静态页面，如果是直接放行
        for (String s : globalUrlProperties.getNoAuthUrl()) {
            boolean match = pathMatcher.match(s, path);
            if (match) {
                return chain.filter(exchange);
            }
        }
        //3、判断请求路径是否是 /api/inner/** 下的，微服务内部互相调用的接口，如果是，直接打回
        for (String s : globalUrlProperties.getDenyUrl()) {
            boolean match = pathMatcher.match(s, path);
            if (match) {
                //直接响应json，告诉前端用户没有权限访问
                Result<String> result = Result.build("", ResultCodeEnum.PERMISSION);
                return responseResult(result,exchange);
            }
        }
        //4、判断是否访问路径是否匹配 /order 等需要登录的模块
        for (String s : globalUrlProperties.getLoginAuthUrl()) {
            boolean match = pathMatcher.match(s, path);
            if (match) {
                //4.1、获取请求中的token，由于前端有可能发送重定向或者异步，因此token有可能在cookie中或者请求头中
                String token = getTokenFromRequest(exchange);
                //4.2、根据token去redis中查找用户信息
                UserInfo userInfo = getUserInfoByToken(token);
                if (userInfo != null) {
                    //4.3、查询到用户，说明token正确，放行，并将userId设置到请求头中，透传userId
                    ServerWebExchange newExchange = userIdOrTempIdTransport(userInfo,exchange);
                    return chain.filter(newExchange);
                }else{
                    //4.4、token错误，直接打回登录页进行登录操作
                    return redirectToCustomPage(globalUrlProperties.getLoginPage() + "?originUrl=" + uri,exchange);
                }
            }
        }
        //5、除了上述请求之外，其余以下皆为普通请求，如果携带有正确的token直接放行，并userId透传
        //5.1、获取token
        String token = getTokenFromRequest(exchange);
        //5.2、验证token
        UserInfo userInfo = getUserInfoByToken(token);
        if (!StringUtils.isEmpty(token) && userInfo == null) {
            //5.3、假请求，直接打回
            return redirectToCustomPage(globalUrlProperties.getLoginPage() + "?originUrl=" + uri,exchange);
        }
        //5.4、透传userId或者userTempId
        exchange = userIdOrTempIdTransport(userInfo, exchange);
        return chain.filter(exchange);
    }

    /**
     * 重定向到登录页
     * @param location  重定向地址
     * @param exchange
     * @return
     */
    private Mono<Void> redirectToCustomPage(String location, ServerWebExchange exchange) {
        //获取响应
        ServerHttpResponse response = exchange.getResponse();
        //封装响应码，重定向【1、设置状态码为302，  2、请求头中设置location，值为重定向到的地址】
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().add(HttpHeaders.LOCATION,location);
        //清除旧的错误的Cookie[token]（同名cookie并max-age=0）解决无限重定向问题
        ResponseCookie responseCookie = ResponseCookie
                .from("token", "suiyixie")
                .path("/")
                .domain(".gmall.com")
                .maxAge(0)
                .build();
        response.getCookies().set("token",responseCookie);
        //响应结束
        return response.setComplete();
    }

    /**
     * 构建没有权限访问的响应
     * @param result  返给前端的无权访问的json字符串
     * @param exchange
     * @return
     */
    private Mono<Void> responseResult(Result<String> result, ServerWebExchange exchange) {
        //获取响应response
        ServerHttpResponse response = exchange.getResponse();
        //设置响应状态码
        response.setStatusCode(HttpStatus.OK);
        //设置响应数据
        String jsonStr = Jsons.toStr(result);
        DataBuffer wrap = response.bufferFactory().wrap(jsonStr.getBytes());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(wrap));
    }

    /**
     * 改变请求的请求头信息，向请求头信息中添加userId，透传userId
     * @param userInfo  用户信息
     * @param exchange  有请求和响应信息
     * @return
     */
    private ServerWebExchange userIdOrTempIdTransport(UserInfo userInfo, ServerWebExchange exchange) {
        //请求一旦发来，所有的请求信息都是固定的，只能读取，不能修改
        //根据原来的请求，封装一个新的请求，变异一个新的请求
        ServerHttpRequest.Builder mutate = exchange.getRequest().mutate();
        //如果用户登录了，则透传用户id
        if (userInfo != null) {
            mutate.header(SysRedisConst.USERID_HEADER, userInfo.getId().toString());
        }
        //如果用户没登录，获取临时id，透传临时id
        String userTempId = getUserTempId(exchange);
        mutate.header(SysRedisConst.USERTEMPID_HEADER, userTempId);
        //根据新的请求构建新的exchange
        ServerWebExchange newExchange = exchange
                .mutate()
                .request(mutate.build())
                .response(exchange.getResponse())
                .build();
        return newExchange;
    }

    /**
     * 获取用户临时id
     * @param exchange
     * @return
     */
    private String getUserTempId(ServerWebExchange exchange) {
        //从cookie中获取userTempId
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("userTempId");
        //避免空指针，先判断cookie是否存在
        if (cookie != null) {
            return cookie.getValue();
        }
        //cookie中不存在userTempId，直接从请求头中获取并返回
        return exchange.getRequest().getHeaders().getFirst("userTempId");
    }

    /**
     * 验证token，根据token获取用户信息
     * @param token  用户token
     * @return
     */
    private UserInfo getUserInfoByToken(String token) {
        String jsonStr = redisTemplate.opsForValue().get(SysRedisConst.USER_LOGIN + token);
        if (!StringUtils.isEmpty(jsonStr)) {
            UserInfo userInfo = Jsons.toObj(jsonStr, UserInfo.class);
            return userInfo;
        }
        return null;
    }

    /**
     * 从请求中获取携带的token
     * @param exchange
     * @return
     */
    private String getTokenFromRequest(ServerWebExchange exchange) {
        String token = "";
        //1、先判断cookies中有没有token
        HttpCookie httpCookie = exchange.getRequest().getCookies().getFirst("token");
        if (httpCookie != null) {
            token = httpCookie.getValue();
            return token;
        }
        //2、cookies中不存在，直接从请求头中取
        token = exchange.getRequest().getHeaders().getFirst("token");
        return token;
    }
}
