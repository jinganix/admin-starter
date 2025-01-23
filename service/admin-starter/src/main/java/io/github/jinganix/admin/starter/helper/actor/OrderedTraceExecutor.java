package io.github.jinganix.admin.starter.helper.actor;

import io.github.jinganix.peashooter.executor.DefaultExecutorSelector;
import io.github.jinganix.peashooter.executor.TraceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderedTraceExecutor
    extends io.github.jinganix.peashooter.executor.OrderedTraceExecutor {

  public OrderedTraceExecutor(TraceExecutor executor, RedissonClient redissonClient) {
    super(
        new DistributedTaskQueueProvider(redissonClient),
        new DefaultExecutorSelector(executor),
        executor.getTracer());
  }
}
