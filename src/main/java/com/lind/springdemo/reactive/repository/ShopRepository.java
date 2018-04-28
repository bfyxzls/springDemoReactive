package com.lind.springdemo.reactive.repository;

import com.lind.springdemo.reactive.model.Shop;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends ReactiveCrudRepository<Shop, String> {
}
