package io.github.jinganix.admin.starter.helper.uid;

/** Snowflake generator. */
public class Snowflake {

  private static final long SEQ_MAGIC = 1021;

  private static final long START_STAMP = 1577808000000L;

  private static final long SEQUENCE_BIT = 12;

  private static final long MACHINE_BIT = 10;

  private static final long DATACENTER_BIT = 0;

  private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

  private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

  private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

  private static final long MACHINE_LEFT = SEQUENCE_BIT;

  private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

  private static final long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

  private final long datacenterId;

  private final long machineId;

  private long sequence = 0L;

  private long lastStamp = -1L;

  /**
   * Constructor.
   *
   * @param datacenterId data center Id
   * @param machineId machined Id
   */
  public Snowflake(long datacenterId, long machineId) {
    if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
      throw new IllegalArgumentException(
          "datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
    }
    if (machineId > MAX_MACHINE_NUM || machineId < 0) {
      throw new IllegalArgumentException(
          "machineId can't be greater than MAX_MACHINE_NUM or less than 0");
    }
    this.datacenterId = datacenterId;
    this.machineId = machineId;
  }

  /**
   * Generate next uid.
   *
   * @return next uid
   */
  public synchronized long nextId() {
    long currStamp = getMillis();
    if (currStamp < lastStamp) {
      throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
    }

    if (currStamp == lastStamp) {
      sequence = (sequence + 1) & MAX_SEQUENCE;
      if (sequence == currStamp % SEQ_MAGIC) {
        currStamp = getNextMillis();
      }
    } else {
      sequence = currStamp % SEQ_MAGIC;
    }

    lastStamp = currStamp;
    return (currStamp - START_STAMP) << TIMESTAMP_LEFT
        | datacenterId << DATACENTER_LEFT
        | machineId << MACHINE_LEFT
        | sequence;
  }

  private long getNextMillis() {
    long millis = getMillis();
    while (millis <= lastStamp) {
      millis = getMillis();
    }
    return millis;
  }

  /**
   * Get currentTimeMillis.
   *
   * @return current millis
   */
  public long getMillis() {
    return System.currentTimeMillis();
  }
}
