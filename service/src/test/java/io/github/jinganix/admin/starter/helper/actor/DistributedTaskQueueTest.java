package io.github.jinganix.admin.starter.helper.actor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.jinganix.peashooter.queue.ExecutionCountStats;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@DisplayName("DistributedTaskQueue")
class DistributedTaskQueueTest {

  private static final class TestableDistributedTaskQueue extends DistributedTaskQueue {

    private TestableDistributedTaskQueue(RedissonClient redissonClient, String lockName) {
      super(redissonClient, lockName);
    }

    @Override
    public boolean tryLock(io.github.jinganix.peashooter.ExecutionStats stats) {
      return super.tryLock(stats);
    }

    @Override
    public boolean shouldYield(io.github.jinganix.peashooter.ExecutionStats stats) {
      return super.shouldYield(stats);
    }

    @Override
    public void unlock() {
      super.unlock();
    }
  }

  @Test
  @DisplayName("should return true when lock acquired")
  void shouldReturnTrueWhenLockAcquired() throws Exception {
    RLock lock = mock(RLock.class);
    RedissonClient redissonClient = mock(RedissonClient.class);
    when(redissonClient.getFairLock("queue")).thenReturn(lock);
    when(lock.tryLock(10L, TimeUnit.SECONDS)).thenReturn(true);
    DistributedTaskQueue queue = new TestableDistributedTaskQueue(redissonClient, "queue");

    assertThat(queue.tryLock(new ExecutionCountStats())).isTrue();
  }

  @Test
  @DisplayName("should return false when interrupted wait")
  void shouldReturnFalseWhenInterruptedWait() throws Exception {
    RLock lock = mock(RLock.class);
    RedissonClient redissonClient = mock(RedissonClient.class);
    when(redissonClient.getFairLock("queue")).thenReturn(lock);
    when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenThrow(new InterruptedException("test"));
    DistributedTaskQueue queue = new TestableDistributedTaskQueue(redissonClient, "queue");

    assertThat(queue.tryLock(new ExecutionCountStats())).isFalse();
  }

  @Test
  @DisplayName("should return true when tenth execution")
  void shouldReturnTrueWhenTenthExecution() {
    RLock lock = mock(RLock.class);
    RedissonClient redissonClient = mock(RedissonClient.class);
    when(redissonClient.getFairLock("queue")).thenReturn(lock);
    DistributedTaskQueue queue = new TestableDistributedTaskQueue(redissonClient, "queue");
    ExecutionCountStats stats = new ExecutionCountStats();
    for (int i = 0; i < 10; i++) {
      stats.record();
    }

    assertThat(queue.shouldYield(stats)).isTrue();
  }

  @Test
  @DisplayName("should return false when non-tenth execution")
  void shouldReturnFalseWhenNonTenthExecution() {
    RLock lock = mock(RLock.class);
    RedissonClient redissonClient = mock(RedissonClient.class);
    when(redissonClient.getFairLock("queue")).thenReturn(lock);
    DistributedTaskQueue queue = new TestableDistributedTaskQueue(redissonClient, "queue");
    ExecutionCountStats stats = new ExecutionCountStats();
    stats.record();

    assertThat(queue.shouldYield(stats)).isFalse();
  }

  @Test
  @DisplayName("should force unlocks when locked queue")
  void shouldForceUnlocksWhenLockedQueue() {
    RLock lock = mock(RLock.class);
    RedissonClient redissonClient = mock(RedissonClient.class);
    when(redissonClient.getFairLock("queue")).thenReturn(lock);
    DistributedTaskQueue queue = new TestableDistributedTaskQueue(redissonClient, "queue");

    queue.unlock();

    verify(lock).forceUnlock();
  }
}
