package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends PagingAndSortingRepository<Goods,Long> {
}
