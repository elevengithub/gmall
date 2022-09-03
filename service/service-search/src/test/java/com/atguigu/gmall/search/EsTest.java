package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class EsTest {

    @Autowired
    ElasticsearchRestTemplate esRestTemplate;
    @Autowired
    PersonRepository personRepository;

    @Test
    public void test03(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchAllQuery());
        SearchHits<Person> search = esRestTemplate.search(queryBuilder.build(), Person.class);
        search.forEach(System.out::println);
    }

    //使用personRepository接口进行简单查询操作
    @Test
    public void test02(){
        //简单查询之查询全部
        Iterable<Person> all = personRepository.findAll();
        all.forEach(System.out::println);
        //简单查询之根据id查询
        Optional<Person> person = personRepository.findById(2l);
        System.out.println(person.get());
        //查询地址在西安市的person
        List<Person> personList = personRepository.findAllByAddressLike("西安市");
        for (Person p : personList) {
            System.out.println(p);
        }
        //查询你领大于23且地址在西安的person
        List<Person> list = personRepository.findAllByAgeGreaterThanAndAddressLike(23,"西安市");
        list.forEach(System.out::println);
    }

    //新增文档到ES索引中
    @Test
    public void test01(){
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("杰");
        person.setLastName("斯");
        person.setAge(23);
        person.setAddress("咸阳市秦都区");
        personRepository.save(person);

        Person person1 = new Person();
        person1.setId(2L);
        person1.setFirstName("卡");
        person1.setLastName("莎");
        person1.setAge(25);
        person1.setAddress("西安市未央区");
        personRepository.save(person1);

        Person person2 = new Person();
        person2.setId(3L);
        person2.setFirstName("奥");
        person2.setLastName("巴马");
        person2.setAge(22);
        person2.setAddress("西安市高新区");
        personRepository.save(person2);

        Person person3 = new Person();
        person3.setId(4L);
        person3.setFirstName("奥");
        person3.setLastName("拉夫");
        person3.setAge(24);
        person3.setAddress("西安市雁塔区");
        personRepository.save(person3);
    }
}
