package com.atguigu.gmall.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfiguration {

    @Bean
    public AlipayClient alipayClient(AliPayProperties properties){
        /*
        String serverUrl, 支付宝网关
        String appId, 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
        String privateKey, 商户私钥，您的PKCS8格式RSA2私钥
        String format, "json"
        String charset, 字符编码格式
        String alipayPublicKey, 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
        String signType, 签名方式
         */
        AlipayClient alipayClient = new DefaultAlipayClient(
                properties.getGatewayUrl(),
                properties.getAppId(),
                properties.getMerchantPrivateKey(),
                "json",
                properties.getCharset(),
                properties.getAlipayPublicKey(),
                properties.getSignType()
        );
        return alipayClient;
    }
}
