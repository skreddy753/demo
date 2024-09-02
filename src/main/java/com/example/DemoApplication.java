package com.example;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class DemoApplication {

  private static Logger log = LoggerFactory.getLogger(DemoApplication.class);

  @PostConstruct
  public void init() {
    log.info("Application started...");
  }

  public static void main(String[] args) {
    log.info("Starting the application...");
    SpringApplication.run(DemoApplication.class, args);
  }

  @GetMapping("/message")
  public String displayMessage() {
    return "Hello Kubernetes!";
  }

}
