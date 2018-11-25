package com.github.bartekbp.cacheabletest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
  @Autowired
  private ItemDao itemDao;

  public Item getItem(int id) {
    return itemDao.read(id);
  }

  @Transactional
  public void simulateAbortedUpdatingItemWithRollback(int id) {
    Item item = itemDao.read(id);
    item.setValue(item.getValue() + 1);
    itemDao.update(id, item);
    throw new RuntimeException("Rollback");
  }
}
