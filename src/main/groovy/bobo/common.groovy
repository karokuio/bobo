package bobo

import groovy.transform.Field
import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.function.Supplier
import net.jodah.failsafe.RetryPolicy
import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.function.CheckedConsumer
import net.jodah.failsafe.function.AsyncCallable
import java.util.concurrent.ConcurrentHashMap

/**
 * @since 0.1.0
 */
@Field
static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(10)

/**
 * Data structure that will hold the statuses of all registered
 * services
 *
 * @since 0.1.0
 */
@Field
static final ConcurrentHashMap<String,String> STORE = new ConcurrentHashMap()

/**
 * @param id
 * @param config
 * @param supplier
 * @since 0.1.0
 */
static void executeRetry(String id, Map config, Supplier<?> supplier) {
  RetryPolicy retryPolicy = new RetryPolicy()
    .retryOn(Exception)
    .withDelay(config.retry.wait_duration, TimeUnit.SECONDS)
    .withMaxRetries(config.retry.max_attempts);

  Failsafe
    .with(retryPolicy)
    .with(EXECUTOR)
    .onFailedAttemptAsync(publishRetry(id))
    .onSuccessAsync(publishSuccess(id))
    .onFailureAsync(publishFailure(id))
    .getAsync { execution ->
      supplier.get()
    }
}

/**
 * @param id
 * @return
 * @since 0.1.0
 */
static CheckedConsumer<? extends Throwable> publishRetry(String id) {
  return { x ->
    STORE.put(id, "RETRIED")
  }
}

/**
 * @param id
 * @return
 * @since 0.1.0
 */
static CheckedConsumer<? extends Throwable> publishSuccess(String id) {
  return { x ->
    STORE.put(id, "SUCCEEDED")
  }
}

/**
 * @param id
 * @return
 * @since 0.1.0
 */
static CheckedConsumer<? extends Throwable> publishFailure(String id) {
  return { x ->
    STORE.put(id, "FAILED")
  }
}
