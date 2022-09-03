package com.atguigu.gmall.search.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "person",shards = 1,replicas = 1)
public class Person {
    @Id
    private Long id;
    @Field(value = "first",type = FieldType.Keyword)
    private String firstName;
    @Field(value = "last",type = FieldType.Keyword)
    private String lastName;
    @Field(value = "age",type = FieldType.Long)
    private Integer age;
    @Field(value = "address",type = FieldType.Text,analyzer = "ik_smart")
    private String address;
}
