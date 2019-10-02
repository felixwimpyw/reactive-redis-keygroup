package com.wimpy.redis.reactive;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import com.wimpy.processor.StringProcessor;
import com.wimpy.redis.model.CacheObject;
import com.wimpy.redis.natives.CacheKeygroupService;

import reactor.core.publisher.Mono;

@Service
public class ReactiveCacheKeygroupServiceImpl implements ReactiveCacheKeygroupService {

  private static final Long EXPECTED_DELETED = 1L;

  private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

  private CacheKeygroupService cacheKeygroupService;
  
  private StringProcessor stringProcessor;
  
  @Override
  public <T> Mono<Boolean> createCache(CacheObject cacheObject, T value) {
    return Mono.fromCallable(() -> cacheObject)
        .map(obj -> obj.getKeys())
        .filter(keys -> keys.length != 0)
        .map(keys -> value)
        .map(stringProcessor::writeAsString)
        .map(content -> cacheKeygroupService.doCreateKey(cacheObject, content))
        .defaultIfEmpty(false);
  }

  @Override
  public <T> Mono<T> getValue(CacheObject cacheObject, Class<T> clazz) {
    return reactiveRedisTemplate.opsForValue()
        .get(cacheObject.getRealKey())
        .map(content -> stringProcessor.readValue(content, clazz));
  }

  @Override
  public <T> Mono<List<T>> getListValue(CacheObject cacheObject, Class<T> clazz) {
    return reactiveRedisTemplate.opsForValue()
        .get(cacheObject.getRealKey())
        .map(content -> stringProcessor.readListValue(content, clazz));
  }

  @Override
  public Mono<Boolean> delete(CacheObject cacheObject) {
    return reactiveRedisTemplate
        .opsForValue()
        .delete(cacheObject.getRealKey())
        .filter(Boolean.TRUE::equals)
        .flatMap(e -> reactiveRedisTemplate.opsForZSet()
            .remove(cacheObject.getKeyGroupSet(), cacheObject.getRealKey()))
        .map(EXPECTED_DELETED::equals);
  }

  @Override
  public Mono<Boolean> deleteAllByPrefix(CacheObject cacheObject) {
    return Mono.fromCallable(() -> cacheObject)
        .map(cacheKeygroupService::deleteAllByPrefix);
  }

  @Override
  public Mono<Boolean> flushAll() {
    return reactiveRedisTemplate.getConnectionFactory()
        .getReactiveConnection()
        .serverCommands()
        .flushAll()
        .hasElement();
  }

}
