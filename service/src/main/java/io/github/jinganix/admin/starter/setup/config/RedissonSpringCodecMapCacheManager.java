package io.github.jinganix.admin.starter.setup.config;

import java.util.Map;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;

public class RedissonSpringCodecMapCacheManager extends RedissonSpringCacheManager {

  private final RedissonClient redisson;

  private final Map<String, Codec> codecMap;

  public RedissonSpringCodecMapCacheManager(
      RedissonClient redisson,
      Map<String, ? extends CacheConfig> config,
      Map<String, Codec> codecMap) {
    super(redisson, config);
    this.redisson = redisson;
    this.codecMap = codecMap;
  }

  @Override
  protected RMap<Object, Object> getMap(String name, CacheConfig config) {
    Codec codec = codecMap.get(name);
    if (codec == null) {
      throw new IllegalArgumentException("No codec found for name: " + name);
    }
    return redisson.getMap(name, codec);
  }

  @Override
  protected RMapCache<Object, Object> getMapCache(String name, CacheConfig config) {
    Codec codec = codecMap.get(name);
    if (codec == null) {
      throw new IllegalArgumentException("No codec found for name: " + name);
    }
    return redisson.getMapCache(name, codec);
  }
}
