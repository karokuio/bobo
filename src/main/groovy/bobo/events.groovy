package bobo

import io.github.resilience4j.core.EventConsumer
import io.github.resilience4j.retry.event.*

static EventConsumer<RetryOnSuccessEvent> success(String event) {
  return { ev ->
    println "$event is OK"
  } as EventConsumer<RetryOnSuccessEvent>
}

static EventConsumer<RetryOnErrorEvent> failure(String event) {
  return { ev ->
    println "$event is KO"
  } as EventConsumer<RetryOnErrorEvent>
}
