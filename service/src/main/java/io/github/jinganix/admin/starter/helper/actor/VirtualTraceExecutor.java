package io.github.jinganix.admin.starter.helper.actor;

import io.github.jinganix.peashooter.Tracer;
import io.github.jinganix.peashooter.executor.TraceExecutor;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

@Component
public class VirtualTraceExecutor extends TraceExecutor {

  public VirtualTraceExecutor(Tracer tracer) {
    super(Executors.newVirtualThreadPerTaskExecutor(), tracer);
  }
}
