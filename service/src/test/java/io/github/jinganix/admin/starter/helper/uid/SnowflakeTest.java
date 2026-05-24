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
  @DisplayName("when constructing with invalid ids")
  class WhenConstructingWithInvalidIds {

    @Test
    @DisplayName("should should throw exception when negative datacenter id")
    void shouldShouldThrowExceptionWhenNegativeDatacenterId() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(-1L, MACHINE_ID));
    }

    @Test
    @DisplayName("should should throw exception when datacenter id above max")
    void shouldShouldThrowExceptionWhenDatacenterIdAboveMax() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(1L, MACHINE_ID));
    }

    @Test
    @DisplayName("should should throw exception when negative machine id")
    void shouldShouldThrowExceptionWhenNegativeMachineId() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(0L, -1L));
    }

    @Test
    @DisplayName("should should throw exception when machine id above max")
    void shouldShouldThrowExceptionWhenMachineIdAboveMax() {
      assertThrows(IllegalArgumentException.class, () -> new Snowflake(0L, 1024L));
    }
  }

  @Nested
  @DisplayName("when generating next id")
  class WhenGeneratingNextId {

    @Test
    @DisplayName("should should encode timestamp machine and sequence when first call")
    void shouldShouldEncodeTimestampMachineAndSequenceWhenFirstCall() {
      long currentMillis = 1_500L;
      FixedTimeSnowflake snowflake = new FixedTimeSnowflake(0L, MACHINE_ID, currentMillis);

      long id = snowflake.nextId();

      long expectedSequence = currentMillis % 1021;
      assertThat(id & SEQUENCE_MASK).isEqualTo(expectedSequence);
      assertThat((id >> 12) & MACHINE_MASK).isEqualTo(MACHINE_ID);
    }

    @Test
    @DisplayName("should should generate distinct ids when same millisecond")
    void shouldShouldGenerateDistinctIdsWhenSameMillisecond() {
      FixedTimeSnowflake snowflake = new FixedTimeSnowflake(0L, MACHINE_ID, 2_000L);

      long firstId = snowflake.nextId();
      long secondId = snowflake.nextId();

      assertThat(secondId).isNotEqualTo(firstId);
      assertThat(secondId & SEQUENCE_MASK).isNotEqualTo(firstId & SEQUENCE_MASK);
    }

    @Test
    @DisplayName("should should throw exception when clock moved backwards")
    void shouldShouldThrowExceptionWhenClockMovedBackwards() {
      FixedTimeSnowflake snowflake = new FixedTimeSnowflake(0L, MACHINE_ID, 2_000L);
      snowflake.nextId();
      snowflake.setMillis(1_999L);

      assertThrows(RuntimeException.class, snowflake::nextId);
    }

    @Test
    @DisplayName("should should advance beyond last stamp when getNextMillis")
    void shouldShouldAdvanceBeyondLastStampWhenGetNextMillis() {
      AdvancingTimeSnowflake snowflake = new AdvancingTimeSnowflake(0L, MACHINE_ID, 1_000L);
      org.springframework.test.util.ReflectionTestUtils.setField(snowflake, "lastStamp", 1_000L);

      assertThat(snowflake.getNextMillis()).isGreaterThan(1_000L);
    }

    @Test
    @DisplayName("should should advance timestamp when sequence magic")
    void shouldShouldAdvanceTimestampWhenSequenceMagic() {
      AdvancingTimeSnowflake snowflake = new AdvancingTimeSnowflake(0L, MACHINE_ID, 1_021L);
      org.springframework.test.util.ReflectionTestUtils.setField(snowflake, "lastStamp", 1_021L);
      org.springframework.test.util.ReflectionTestUtils.setField(snowflake, "sequence", 4_095L);

      assertThat(snowflake.nextId()).isNotZero();
    }
  }

  @Test
  @DisplayName("should should return current time millis when call")
  void shouldShouldReturnCurrentTimeMillisWhenCall() {
    Snowflake snowflake = new Snowflake(0L, MACHINE_ID);
    long before = System.currentTimeMillis();

    long millis = snowflake.getMillis();

    assertThat(millis).isBetween(before, System.currentTimeMillis());
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
