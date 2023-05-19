package com.isxcode.star.yun.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class SparkYunAgentApplication {

  public static void main(String[] args) {

    SpringApplication.run(SparkYunAgentApplication.class, args);
  }
}
