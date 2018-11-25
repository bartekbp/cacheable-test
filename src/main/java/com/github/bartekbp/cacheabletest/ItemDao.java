package com.github.bartekbp.cacheabletest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Repository
public class ItemDao {
  private static final Logger log = LoggerFactory.getLogger(ItemDao.class);
  private static final String CACHE_NAME = "item";

  @Autowired
  private DataSource dataSource;

  @Transactional
  @Cacheable(value = CACHE_NAME)
  public Item read(int id) {
    log.info("Dao called to read item");
    JdbcTemplate template = new JdbcTemplate(dataSource);
    return template.queryForObject("SELECT * FROM item WHERE id = ?", new Object[] {id} , new BeanPropertyRowMapper<>(Item.class));
  }

  @Transactional
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public void update(int id, Item item) {
    JdbcTemplate template = new JdbcTemplate(dataSource);
    template.update("UPDATE item SET value = ? WHERE id = ?", new Object[] {item.getValue(), id});
    log.info("Item updated, new value: {}", id, item.getValue());
  }
}
