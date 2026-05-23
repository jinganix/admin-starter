package io.github.jinganix.admin.starter.helper.jooq;

import org.jooq.Condition;
import org.jooq.impl.DSL;

/** Build jooq query condition. */
public class ConditionBuilder {

  private Condition condition;

  public static ConditionBuilder builder() {
    return new ConditionBuilder();
  }

  public ConditionBuilder and(Condition other) {
    if (other != null) {
      condition = condition == null ? other : condition.and(other);
    }
    return this;
  }

  public ConditionBuilder or(Condition other) {
    if (other != null) {
      condition = condition == null ? other : condition.or(other);
    }
    return this;
  }

  public Condition build() {
    return condition == null ? DSL.noCondition() : condition;
  }

  public Condition nullable() {
    return condition;
  }
}
