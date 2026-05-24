package io.github.jinganix.admin.starter.setup.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.spring.cache.CacheConfig;

@DisplayName("RedissonSpringCodecMapCacheManager")
class RedissonSpringCodecMapCacheManagerTest {

  static class ExposedManager extends RedissonSpringCodecMapCacheManager {

    ExposedManager(
        RedissonClient redisson, Map<String, CacheConfig> config, Map<String, Codec> codecMap) {
      super(redisson, config, codecMap);
    }

    RMap<Object, Object> exposeMap(String name) {
      return getMap(name, new CacheConfig());
    }

    RMapCache<Object, Object> exposeMapCache(String name) {
      return getMapCache(name, new CacheConfig());
    }
  }

  @Nested
  @DisplayName("getMap")
  class GetMap {

    @Test
    @DisplayName("Given missing codec for cache name -> IllegalArgumentException")
    void givenMissingCodec() {
      // Given
      RedissonClient redisson = mock(RedissonClient.class);
      ExposedManager manager = new ExposedManager(redisson, Map.of(), Map.of());

      // When / Then
      assertThatThrownBy(() -> manager.exposeMap("unknown"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("No codec found for name: unknown");
    }

    @Test
    @DisplayName("Given registered codec -> returns map with codec")
    void givenRegisteredCodec() {
      // Given
      RedissonClient redisson = mock(RedissonClient.class);
      Codec codec = mock(Codec.class);
      @SuppressWarnings("unchecked")
      RMap<Object, Object> map = (RMap<Object, Object>) mock(RMap.class);
      when(redisson.getMap("cache-a", codec)).thenReturn(map);
      ExposedManager manager =
          new ExposedManager(
              redisson, Map.of("cache-a", new CacheConfig()), Map.of("cache-a", codec));

      // When
      RMap<Object, Object> result = manager.exposeMap("cache-a");

      // Then
      assertThat(result).isSameAs(map);
    }
  }

  @Nested
  @DisplayName("getMapCache")
  class GetMapCache {

    @Test
    @DisplayName("Given missing codec for cache name -> IllegalArgumentException")
    void givenMissingCodec() {
      // Given
      RedissonClient redisson = mock(RedissonClient.class);
      ExposedManager manager = new ExposedManager(redisson, Map.of(), Map.of());

      // When / Then
      assertThatThrownBy(() -> manager.exposeMapCache("unknown"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("No codec found for name: unknown");
    }
  }
}
