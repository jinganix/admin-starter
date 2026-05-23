package io.github.jinganix.admin.starter.helper.uid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Snowflake")
class SnowflakeTest {

  private static final long MACHINE_ID = 3L;
  private static final long SEQUENCE_MASK = (1L << 12) - 1;
  private static final long MACHINE_MASK = (1L << 10) - 1;

  @Nested
  @DisplayName("constructor")
  class Constructor {

    @Test
    @DisplayName("Given negative datacenter id -> should throw exception")
    void givenNegativeDatacenterIdShouldThrowException() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(-1L, MACHINE_ID));
    }

    @Test
    @DisplayName("Given datacenter id above max -> should throw exception")
    void givenDatacenterIdAboveMaxShouldThrowException() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(1L, MACHINE_ID));
    }

    @Test
    @DisplayName("Given negative machine id -> should throw exception")
    void givenNegativeMachineIdShouldThrowException() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(0L, -1L));
    }

    @Test
    @DisplayName("Given machine id above max -> should throw exception")
    void givenMachineIdAboveMaxShouldThrowException() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(0L, 1024L));
    }
  }

  @Nested
  @DisplayName("nextId")
  class NextId {

    @Test
    @DisplayName("Given first call -> should encode timestamp machine and sequence")
    void givenFirstCallShouldEncodeTimestampMachineAndSequence() {
      long currentMillis = 1_500L;
      FixedTimeSnowflake snowflake = new FixedTimeSnowflake(0L, MACHINE_ID, currentMillis);

      long id = snowflake.nextId();

      long expectedSequence = currentMillis % 1021;
      assertThat(id & SEQUENCE_MASK).isEqualTo(expectedSequence);
      assertThat((id >> 12) & MACHINE_MASK).isEqualTo(MACHINE_ID);
    }

    @Test
    @DisplayName("Given same millisecond -> should generate distinct ids")
    void givenSameMillisecondShouldGenerateDistinctIds() {
      FixedTimeSnowflake snowflake = new FixedTimeSnowflake(0L, MACHINE_ID, 2_000L);

      long firstId = snowflake.nextId();
      long secondId = snowflake.nextId();

      assertThat(secondId).isNotEqualTo(firstId);
      assertThat(secondId & SEQUENCE_MASK).isNotEqualTo(firstId & SEQUENCE_MASK);
    }

    @Test
    @DisplayName("Given clock moved backwards -> should throw exception")
    void givenClockMovedBackwardsShouldThrowException() {
      FixedTimeSnowflake snowflake = new FixedTimeSnowflake(0L, MACHINE_ID, 2_000L);
      snowflake.nextId();
      snowflake.setMillis(1_999L);

      assertThrows(RuntimeException.class, snowflake::nextId);
    }

    @Test
    @DisplayName("Given getNextMillis -> should advance beyond last stamp")
    void givenGetNextMillisShouldAdvanceBeyondLastStamp() {
      AdvancingTimeSnowflake snowflake = new AdvancingTimeSnowflake(0L, MACHINE_ID, 1_000L);
      org.springframework.test.util.ReflectionTestUtils.setField(snowflake, "lastStamp", 1_000L);

      assertThat(snowflake.getNextMillis()).isGreaterThan(1_000L);
    }

    @Test
    @DisplayName("Given sequence magic -> should advance timestamp")
    void givenSequenceMagicShouldAdvanceTimestamp() {
      AdvancingTimeSnowflake snowflake = new AdvancingTimeSnowflake(0L, MACHINE_ID, 1_021L);
      org.springframework.test.util.ReflectionTestUtils.setField(snowflake, "lastStamp", 1_021L);
      org.springframework.test.util.ReflectionTestUtils.setField(snowflake, "sequence", 4_095L);

      assertThat(snowflake.nextId()).isNotZero();
    }
  }

  @Nested
  @DisplayName("getMillis")
  class GetMillis {

    @Test
    @DisplayName("Given call -> should return current time millis")
    void givenCallShouldReturnCurrentTimeMillis() {
      Snowflake snowflake = new Snowflake(0L, MACHINE_ID);
      long before = System.currentTimeMillis();

      long millis = snowflake.getMillis();

      assertThat(millis).isBetween(before, System.currentTimeMillis());
    }
  }

  private static final class FixedTimeSnowflake extends Snowflake {

    private long millis;

    private FixedTimeSnowflake(long datacenterId, long machineId, long millis) {
      super(datacenterId, machineId);
      this.millis = millis;
    }

    private void setMillis(long millis) {
      this.millis = millis;
    }

    @Override
    public long getMillis() {
      return millis;
    }
  }

  private static final class AdvancingTimeSnowflake extends Snowflake {

    private long millis;

    private int pollCount;

    private AdvancingTimeSnowflake(long datacenterId, long machineId, long millis) {
      super(datacenterId, machineId);
      this.millis = millis;
    }

    @Override
    public long getMillis() {
      pollCount++;
      return pollCount == 1 ? millis : millis + pollCount;
    }
  }
}
