package com.wimpy.redis.model;

public class CacheObject {

  private final String keyGroup;

  private final String[] keys;

  private final String realKey;

  private final String keyGroupSet;

  private final String keyGroupLock;

  private CacheObject(String keyGroup, String[] keys, String realKey, String keyGroupSet,
      String keyGroupLock) {
    super();
    this.keyGroup = keyGroup;
    this.keys = keys;
    this.realKey = realKey;
    this.keyGroupSet = keyGroupSet;
    this.keyGroupLock = keyGroupLock;
  }

  public String getRealKey() {
    return realKey;
  }

  public String getKeyGroupSet() {
    return keyGroupSet;
  }

  public String getKeyGroupLock() {
    return keyGroupLock;
  }

  public String getKeyGroup() {
    return keyGroup;
  }

  public String[] getKeys() {
    return keys;
  }

  public static class Builder {

    private final String SET_SUFFIX = "~keys";

    private final String LOCK_SUFFIX = "~lock";

    private final String DELIMITER = "-";

    private final String keyGroup;

    private final String[] keys;

    public Builder(String keyGroup, String... keys) {
      this.keyGroup = keyGroup;
      this.keys = keys;
    }

    public CacheObject build() {
      return new CacheObject(keyGroup, keys, generateRealKey(), getKeyGroupSet(),
          getKeyGroupLock());
    }

    private String generateRealKey() {
      StringBuilder sb = new StringBuilder();
      for (String key : keys) {
        sb.append(DELIMITER);
        sb.append(key);
      }
      return keyGroup + sb.toString();
    }

    private String getKeyGroupSet() {
      return keyGroup + SET_SUFFIX;
    }

    private String getKeyGroupLock() {
      return keyGroup + LOCK_SUFFIX;
    }

  }

}
