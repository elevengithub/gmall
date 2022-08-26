package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class MinioTest {
     @Test
     public void test(){
         try {
             // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
             MinioClient minioClient = new MinioClient("http://192.168.6.100:9000", "admin", "admin123456");

             // 检查存储桶是否已经存在
             boolean isExist = minioClient.bucketExists("gmall");
             if(isExist) {
                 System.out.println("Bucket already exists.");
             } else {
                 // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                 minioClient.makeBucket("gmall");
             }

             // 使用putObject上传一个文件到存储桶中。
             InputStream inputStream = new FileInputStream("E:\\220310Java\\尚品汇\\资料\\03 商品图片\\品牌\\oppo.png");
             PutObjectOptions options = new PutObjectOptions(inputStream.available(), -1);
             options.setContentType("image/png");
             minioClient.putObject("gmall","oppo.png",inputStream,options);
             System.out.println("/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
         } catch(Exception e) {
             System.out.println("Error occurred: " + e);
         }
     }
}
