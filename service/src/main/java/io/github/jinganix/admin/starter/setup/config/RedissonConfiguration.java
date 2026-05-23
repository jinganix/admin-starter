package io.github.jinganix.admin.starter.setup.config;

import static java.util.concurrent.TimeUnit.DAYS;

import io.github.jinganix.admin.starter.helper.redisson.JsonCodec;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserToken;
import io.github.jinganix.admin.starter.sys.utils.CacheConst;
import java.util.HashMap;
import java.util.Map;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.data.redis.url")
public class RedissonConfiguration {

  @Bean(destroyMethod = "shutdown")
  RedissonClient redisson(@Value("${spring.data.redis.url}") String address) {
    Config config = new Config();
    config
        .useSingleServer()
        .setAddress(address)
        .setConnectionMinimumIdleSize(5)
        .setConnectionPoolSize(10);
    return Redisson.create(config);
  }

  @Bean
  CacheManager cacheManager(RedissonClient redissonClient) {
    Map<String, CacheConfig> config = new HashMap<>();
    Map<String, Codec> codecMap = new HashMap<>();
    CacheConfig tokenConfig = new CacheConfig(0, DAYS.toMillis(3));
    config.put(CacheConst.ADMIN_USER_TOKEN, tokenConfig);
    codecMap.put(CacheConst.ADMIN_USER_TOKEN, new JsonCodec(AdminUserToken.class));
    return new RedissonSpringCodecMapCacheManager(redissonClient, config, codecMap);
  }
}
