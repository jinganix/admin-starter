package io.github.jinganix.admin.starter.helper.actor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.jinganix.peashooter.TaskQueueProvider;
import io.github.jinganix.peashooter.queue.TaskQueue;
import java.time.Duration;
import org.redisson.api.RedissonClient;

public class DistributedTaskQueueProvider implements TaskQueueProvider {

  private final LoadingCache<String, TaskQueue> queues;

  public DistributedTaskQueueProvider(RedissonClient redissonClient) {
    this.queues =
        Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .build(key -> new DistributedTaskQueue(redissonClient, key));
  }

  @Override
  public TaskQueue get(String key) {
    return queues.get(key);
  }
}
