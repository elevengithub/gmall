package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) throws Exception {
        //1、创建MinioClient，通过配置类已经自动注入到容器中，直接取。
        //2、判断bucket是否存在，配置类中已经处理。
        //3、上传文件
        //file.getOriginalFilename()上传的文件名  file.getName()前台输入框的name属性值
        InputStream inputStream = file.getInputStream();
        PutObjectOptions options = new PutObjectOptions(file.getSize(),-1);
        //生成唯一文件名，防止文件覆盖
        String fileName = UUID.randomUUID().toString().replace("-", "") + file.getOriginalFilename();
        //设置上传后存入的文件夹，以上传时间为文件夹
        String date = DateUtil.formatDate(new Date());
        fileName = date + "/" + fileName;
        //设置类型为图片
        options.setContentType(minioProperties.getContentType());
        minioClient.putObject(minioProperties.getBucketName(),fileName,inputStream,options);
        //4、返回文件地址值url
        String url = minioProperties.getEndpoint()
                    + "/" + minioProperties.getBucketName()
                    + "/" + fileName;
        return url;
    }
}
