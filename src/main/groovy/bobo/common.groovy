package bobo

import java.time.Duration
import io.github.resilience4j.retry.RetryConfig

static RetryConfig newRetryConfig(Map config) {
 return RetryConfig
    .custom()
    .maxAttempts(config.retry.max_attempts)
    .waitDuration(Duration.ofSeconds(config.retry.wait_duration))
    .build()
}
