package com.wimpy.redis.reactive;

import java.util.List;

import com.wimpy.redis.model.CacheObject;

import reactor.core.publisher.Mono;

public interface ReactiveCacheKeygroupService {

  <T> Mono<Boolean> createCache(CacheObject cacheObject, T value);

  <T> Mono<T> getValue(CacheObject cacheObject, Class<T> clazz);

  <T> Mono<List<T>> getListValue(CacheObject cacheObject, Class<T> clazz);

  Mono<Boolean> delete(CacheObject cacheObject);

  Mono<Boolean> deleteAllByPrefix(CacheObject cacheObject);

  Mono<Boolean> flushAll();
}
