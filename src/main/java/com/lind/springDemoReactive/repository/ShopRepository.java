package com.lind.springDemoReactive.repository;

import com.lind.springDemoReactive.model.Shop;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends ReactiveCrudRepository<Shop, String> {
}
