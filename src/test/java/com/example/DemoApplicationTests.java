package com.example;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

  private static Logger log = LoggerFactory.getLogger(DemoApplicationTests.class);

  @Test
  public void contextLoads() {
    log.info("Test case executing...");
    assertEquals(1, 1);
  }

}
