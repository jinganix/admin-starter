package io.github.jinganix.admin.starter.helper.actor;

import io.github.jinganix.peashooter.executor.DefaultExecutorSelector;
import io.github.jinganix.peashooter.executor.TraceExecutor;
import io.github.jinganix.peashooter.queue.CaffeineTaskQueueProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderedTraceExecutor
    extends io.github.jinganix.peashooter.executor.OrderedTraceExecutor {

  public OrderedTraceExecutor(
      TraceExecutor executor, @Autowired(required = false) RedissonClient redissonClient) {
    super(
        redissonClient == null
            ? new CaffeineTaskQueueProvider()
            : new DistributedTaskQueueProvider(redissonClient),
        new DefaultExecutorSelector(executor),
        executor.getTracer());
  }
}
