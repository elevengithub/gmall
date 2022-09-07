package com.atguigu.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class GlobalUrlProperties {
    private List<String> noAuthUrl;
    private List<String> loginAuthUrl;
    private List<String> denyUrl;
    private String loginPage;
}
