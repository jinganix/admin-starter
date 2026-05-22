package io.github.jinganix.admin.starter.helper.actor;

import io.github.jinganix.peashooter.ExecutionStats;
import io.github.jinganix.peashooter.queue.ExecutionCountStats;
import io.github.jinganix.peashooter.queue.LockableTaskQueue;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public class DistributedTaskQueue extends LockableTaskQueue {

  private static final int LOCK_SECONDS = 10;

  private final RLock lock;

  public DistributedTaskQueue(RedissonClient redissonClient, String lockName) {
    super(new ExecutionCountStats());
    this.lock = redissonClient.getFairLock(lockName);
  }

  @Override
  protected boolean tryLock(ExecutionStats stats) {
    try {
      return lock.tryLock(LOCK_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      return false;
    }
  }

  @Override
  protected boolean shouldYield(ExecutionStats stats) {
    return ((ExecutionCountStats) stats).getExecutionCount() % 10 == 0;
  }

  @Override
  protected void unlock() {
    lock.forceUnlock();
  }
}
