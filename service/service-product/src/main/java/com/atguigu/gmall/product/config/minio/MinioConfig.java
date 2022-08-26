package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception{
        //创建minioClient连接
        MinioClient minioClient = new MinioClient(
                minioProperties.getEndpoint(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey());
        //判断bucket是否存在
        boolean gmall = minioClient.bucketExists(minioProperties.getBucketName());
        if (!gmall) {
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        return minioClient;
    }
}
