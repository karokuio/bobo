package bobo

import java.time.Duration
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig

import bobo.events as E

static Retry newRetry(String id, Map config) {
  RetryConfig retryCfg = C.newRetryConfig(config)
  Retry retry = Retry.of(id, retryCfg)

  return retry
  .eventPublisher
  .onSuccess(E.success(id))
  .onError(E.failure(id))
  .onIgnoredError(E.failure(id))
}

static RetryConfig newRetryConfig(Map config) {
 return RetryConfig
    .custom()
    .maxAttempts(config.retry.max_attempts)
    .waitDuration(Duration.ofSeconds(config.retry.wait_duration))
    .build()
}
