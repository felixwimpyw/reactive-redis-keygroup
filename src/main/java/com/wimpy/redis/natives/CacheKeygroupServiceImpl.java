package com.wimpy.redis.natives;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import com.wimpy.redis.model.CacheObject;
import com.wimpy.redis.model.CacheProperties;
import com.wimpy.redis.reactive.ReactiveCacheKeygroupServiceImpl;

import reactor.core.publisher.Mono;

public class CacheKeygroupServiceImpl implements CacheKeygroupService {

  private static final Logger log = LoggerFactory.getLogger(CacheKeygroupServiceImpl.class);

  private static final int LOCK_EXPIRY_MILLIS = 10000;

  private static final Long STARTING_INCR = 1L;

  private static final Double DEFAULT_SCORE = 1D;

  private RedisTemplate<String, String> redisTemplate;

  private CacheProperties cacheProperties;
  
  public CacheKeygroupServiceImpl(RedisTemplate<String, String> redisTemplate,
      CacheProperties cacheProperties) {
    this.redisTemplate = redisTemplate;
    this.cacheProperties = cacheProperties;
  }

  @Override
  public boolean doCreateKey(CacheObject cacheObject, String content) {
    redisTemplate.multi();
    redisTemplate.opsForZSet().add(cacheObject.getKeyGroupSet(), cacheObject.getRealKey(),
        DEFAULT_SCORE);
    redisTemplate.opsForValue().set(cacheObject.getRealKey(), content,
        Duration.ofMillis(cacheProperties.getExpiryInMillis()));
    List<Object> result = redisTemplate.exec();
    return !result.isEmpty();
  }

  @Override
  public boolean delete(CacheObject cacheObject) {
    redisTemplate.delete(cacheObject.getRealKey());
    redisTemplate.opsForZSet().remove(cacheObject.getKeyGroupSet(), cacheObject.getRealKey());
    return true;
  }
  
  @Override
  public boolean deleteAllByPrefix(CacheObject cacheObject) {
    Set<String> keys = null;
    long start = 0L;
    Long increment = redisTemplate.opsForValue().increment(cacheObject.getKeyGroupLock());
    if (isAllowed(increment)) {
      try {
        redisTemplate.expire(cacheObject.getKeyGroupLock(), LOCK_EXPIRY_MILLIS,
            TimeUnit.MILLISECONDS);
        do {
          keys = redisTemplate.opsForZSet().range(cacheObject.getKeyGroupSet(), start,
              cacheProperties.getClearSize() - 1);
          if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
            redisTemplate.opsForZSet().removeRange(cacheObject.getKeyGroupSet(), start,
                keys.size() - 1);
          }
        } while (keys.size() == cacheProperties.getClearSize());
        return true;
      } catch (Exception e) {
        log.error("#deleteAllByPrefix failed on cacheObj = {}, err = ", cacheObject, e);
        return false;
      } finally {
        redisTemplate.delete(cacheObject.getKeyGroupLock());
      }
    }
    return false;
  }

  private boolean isAllowed(Long increment) {
    return STARTING_INCR.equals(increment);
  }

}
