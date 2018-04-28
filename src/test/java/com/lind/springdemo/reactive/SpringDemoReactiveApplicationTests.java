package com.lind.springdemo.reactive;

import com.lind.springdemo.reactive.model.Shop;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringDemoReactiveApplicationTests {


  private static final List<String> WORDS = Arrays.asList(
      "the",
      "quick",
      "brown",
      "fox",
      "jumped",
      "over",
      "the",
      "lazy",   
      "dog"
  );

  ;
  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private ReactiveMongoTemplate reactiveMongoTemplate;
  private double initPrice = 30.0;

  @Test
  public void get() {
    webTestClient.get().uri("/home")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(response ->
            Assertions.assertThat(response.getResponseBody()).isNotNull());

  }

  @Test
  public void post() {
    val shop = new Shop();
    shop.setName("testzzl");
    shop.setCreateAt(new Date(2018, 1, 1));
    webTestClient.post().uri("/home")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .body(Mono.just(shop), Shop.class)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody();
  }

  @Test
  public void del() {
    webTestClient.delete()
        .uri("/home/{id}", "5ae1c42d61f2003c7b7faa28")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void patch() {
    val shop = new Shop();
    shop.setName("modify2");
    webTestClient.patch()
        .uri("/home/{id}", "5ae19cc561f200393992ca85")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .body(Mono.just(shop), Shop.class)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody();

  }

  @Test
  public void test() {

    Flux<Integer> ints = Flux.range(1, 4)
        .map(i -> {
          if (i <= 3) {
            return i;
          }
          throw new RuntimeException("Got to 4");
        });
    ints.subscribe(i -> System.out.println(i),
        error -> System.err.println("Error: " + error));
  }

  @Test
  public void test2() throws Exception {
    Flux<Integer> alphabet = Flux.just(-1, 30, 13, 9, 20)
        .handle((i, sink) -> {
          int letter = Math.abs(i);
          sink.next(letter);

        });

    alphabet.subscribe(System.out::println);

  }

  @Test
  public void testBasic() {
    Flux.just("Hello", "World").subscribe(System.out::println);
    Flux.fromArray(new Integer[] {1, 2, 3}).subscribe(System.out::println);
    Flux.empty().subscribe(System.out::println);
    Flux.range(1, 10).subscribe(System.out::println);
    Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
  }

  @Test
  public void testMonoBasic() {
    Flux.just("Hello", "OK").map(i -> {
      if (i == "Hello") {
        return i;
      }
      throw new RuntimeException("Not Hello!");
    }).subscribe(System.out::println);
  }

  ;

  @Test
  public void testFlux() {

    AtomicInteger ai = new AtomicInteger();
    Function<Flux<String>, Flux<String>> filterAndMap = f -> {
      if (ai.incrementAndGet() == 1) {
        return f.filter(color -> !color.equals("orange"))
            .map(String::toUpperCase);
      }
      return f.filter(color -> !color.equals("purple"))
          .map(String::toUpperCase);
    };

    Flux<String> composedFlux =
        Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
            .doOnNext(System.out::println)
            .compose(filterAndMap);

    composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :" + d));
    composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: " + d));
  }

  @Test
  public void test3() {
    Flux<String> flux =
        Flux.<String>error(new IllegalArgumentException())
            .retryWhen(companion -> companion
                .doOnNext(s -> System.out.println(s + " at " + LocalTime.now()))
                .zipWith(Flux.range(1, 4), (error, index) -> {
                  if (index < 4) {
                    return index;
                  } else {
                    throw Exceptions.propagate(error);
                  }
                })
                .flatMap(index -> Mono.delay(Duration.ofMillis(index * 100)))
                .doOnNext(s -> System.out.println("retried at " + LocalTime.now()))
            );
  }

  @Test
  public void findingMissingLetter() {
    List<String> words = new ArrayList<String>();
    words.add("a");
    words.add("b");
    words.add("c");
    Flux<String> manyLetters = Flux
        .fromIterable(words)
        .flatMap(word -> Flux.fromArray(word.split("")))
        .distinct()
        .sort()
        .zipWith(Flux.range(1, Integer.MAX_VALUE),
            (string, count) -> String.format("%2d. %s", count, string));

    manyLetters.subscribe(System.out::println);
  }

  @Test
  public void test5() throws Exception {
    int a = 0;
    int b = 1 / a;
    Assert.assertEquals(a, b);
  }

  @Test
  public void test6() throws AuctionException {
    bid("20");
  }

  public void bid(String bidPrice) throws AuctionException {
    double d = 0.0;
    try {
      d = Double.parseDouble(bidPrice);
    } catch (Exception e) {
      e.printStackTrace();
      throw new AuctionException("竞拍价必须为数值，不能包含其它数值");
    }
    if (initPrice > d) {
      throw new AuctionException("竞拍价应比起拍价高");
    }
    initPrice = d;
  }

}
