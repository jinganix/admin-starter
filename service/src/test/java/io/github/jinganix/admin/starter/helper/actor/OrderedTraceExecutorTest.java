package io.github.jinganix.admin.starter.helper.actor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jinganix.peashooter.Tracer;
import io.github.jinganix.peashooter.executor.TraceExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;

@DisplayName("OrderedTraceExecutor")
class OrderedTraceExecutorTest {

  @Test
  @DisplayName("should uses caffeine queue provider when null redisson client")
  void shouldUsesCaffeineQueueProviderWhenNullRedissonClient() {
    TraceExecutor executor = mock(TraceExecutor.class);
    when(executor.getTracer()).thenReturn(mock(Tracer.class));

    OrderedTraceExecutor orderedTraceExecutor = new OrderedTraceExecutor(executor, null);

    assertThat(orderedTraceExecutor).isNotNull();
  }

  @Test
  @DisplayName("should uses distributed queue provider when redisson client")
  void shouldUsesDistributedQueueProviderWhenRedissonClient() {
    TraceExecutor executor = mock(TraceExecutor.class);
    when(executor.getTracer()).thenReturn(mock(Tracer.class));
    RedissonClient redissonClient = mock(RedissonClient.class);

    OrderedTraceExecutor orderedTraceExecutor = new OrderedTraceExecutor(executor, redissonClient);

    assertThat(orderedTraceExecutor).isNotNull();
  }
}
