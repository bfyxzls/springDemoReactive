package com.lind.springDemoReactive.model;


import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
  @Id
  private String id;
  private String name;
  private Date createAt;
}
