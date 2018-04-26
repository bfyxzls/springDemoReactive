package com.lind.springDemoReactive.controller;

import com.lind.springDemoReactive.model.Shop;
import com.lind.springDemoReactive.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HomeController {

  /**
   * 扩展ReactiveCrudRepository接口，提供基本的CRUD操作
   */
  @Autowired
  private ShopRepository shopRepository;

  /**
   * spring-boot-starter-data-mongodb-reactive提供的通用模板
   */
  @Autowired
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @GetMapping("/home")
  public Flux<Shop> findAll() {
    return reactiveMongoTemplate.findAll(Shop.class);
  }

  @GetMapping("/home/{id}")
  public Mono<Shop> get(@PathVariable String id) {
    return shopRepository.findById(id);
  }

  @PostMapping("/home")
  public Mono<Shop> create(@RequestBody Mono<Shop> restaurants) {
    return reactiveMongoTemplate.insert(restaurants);
  }

  @PatchMapping("/home/{id}")
  public Mono<Shop>  update(@PathVariable String id, @RequestBody Shop restaurants) {
    Query query = new Query(Criteria.where("_id").is(id));
    Update update = Update.update("name", restaurants.getName());

    return reactiveMongoTemplate.findAndModify(query, update, Shop.class);
  }


  @DeleteMapping("/home/{id}")
  public Mono<Void> delete(@PathVariable String id) {
    return shopRepository.deleteById(id);
  }
}
