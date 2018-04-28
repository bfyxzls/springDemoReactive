package com.lind.springdemo.reactive;

public class AuctionException extends Exception {
  //无参构造
  public AuctionException() {}
  //含参构造
  //通过调用父类的构造器将字符串msg传给异常对象的massage属性，
  //massage属性就是对异常的描述
  public AuctionException(String msg) {
    super(msg);
  }
}