package io.github.jinganix.admin.starter.helper.uid;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/** UidGeneratorImpl. */
@Slf4j
@RequiredArgsConstructor
public class SnowflakeGenerator implements UidGenerator {

  @Value("${utils.node-id:1}")
  private long nodeId;

  private Snowflake snowflake;

  /** Initialize. */
  @PostConstruct
  public void initialize() {
    this.snowflake = new Snowflake(0, nodeId);
  }

  /**
   * Next id.
   *
   * @return id
   */
  @Override
  public long nextUid() {
    return snowflake.nextId();
  }
}
