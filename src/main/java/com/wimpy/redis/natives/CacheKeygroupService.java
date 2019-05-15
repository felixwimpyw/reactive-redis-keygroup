package com.wimpy.redis.natives;

import com.wimpy.redis.model.CacheObject;

public interface CacheKeygroupService {

  boolean doCreateKey(CacheObject cacheObject, String content);
  
  boolean deleteAllByPrefix(CacheObject cacheObject);

}
