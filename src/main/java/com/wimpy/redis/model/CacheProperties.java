package com.wimpy.redis.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("redis.cache")
public class CacheProperties {

  private Long expiryInMillis = 3600000L;

  private Long clearSize = 100L;

  public Long getExpiryInMillis() {
    return expiryInMillis;
  }

  public void setExpiryInMillis(Long expiryInMillis) {
    this.expiryInMillis = expiryInMillis;
  }

  public Long getClearSize() {
    return clearSize;
  }

  public void setClearSize(Long clearSize) {
    this.clearSize = clearSize;
  }

}
