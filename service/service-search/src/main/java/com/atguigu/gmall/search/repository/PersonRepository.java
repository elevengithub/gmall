package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//继承CrudRepository<Person,Long>或者其子接口PagingAndSortingRepository<Person,Long>
@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person,Long> {
    //根据方法名称查询
    //查询地址在address的person
    List<Person> findAllByAddressLike(String address);

    //查询年龄大于age且地址在address的person
    List<Person> findAllByAgeGreaterThanAndAddressLike(Integer age, String address);
}
