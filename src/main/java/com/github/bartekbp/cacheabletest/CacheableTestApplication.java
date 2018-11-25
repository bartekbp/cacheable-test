package com.github.bartekbp.cacheabletest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

@SpringBootApplication
@EnableCaching
public class CacheableTestApplication implements CommandLineRunner {
  private static final Logger log = LoggerFactory.getLogger(CacheableTestApplication.class);

  @Autowired
  private ItemService service;

  public static void main(String[] args) {
    SpringApplication.run(CacheableTestApplication.class, args);
  }

  @Override
  public void run(String... args) {
    final int itemId = 1;
    Item item = service.getItem(itemId);
    int itemValue = item.getValue();
    log.info("Read item value: {}", itemValue);

    try {
      service.simulateAbortedUpdatingItemWithRollback(itemId);
    } catch (RuntimeException e) {
      log.error("Transaction rolled back", e);
    }

    log.info("Newly read item value: {}", item.getValue());
    Assert.isTrue(itemValue == item.getValue(), "Item value changed externally");
  }

  @Bean
  CacheManager caffeineCacheManager() {
    return new TransactionAwareCacheManagerProxy(new CaffeineCacheManager());
  }

}
