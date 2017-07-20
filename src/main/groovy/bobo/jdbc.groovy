/**
 * JDBC
 * ====
 *
 * This watcher checks that the configured JDBC connection responds to
 * the check expression defined in the services.yml file
 *
 * @author @marioggar
 * @since 0.1.0
 *
 */
package bobo

import groovy.sql.Sql
import java.util.function.Supplier
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig

import bobo.events as E
import bobo.common as C

/**
 * Watches a given JDBC connection
 *
 * @param config
 * @since 0.1.0
 */
static void watch(Map config) {
  String watcherId = "jdbc:${config.name}"
  RetryConfig retryCfg = C.newRetryConfig(config)
  Retry retry = Retry.of(watcherId, retryCfg)

  retry
  .eventPublisher
  .onSuccess(E.success(watcherId))
  .onError(E.failure(watcherId))
  .onIgnoredError(E.failure(watcherId))

  retry.executeSupplier(newQueryProducer(config))
}

/**
 * @param config
 * @return
 * @since 0.1.0
 */
static Supplier<String> newQueryProducer(Map config) {
  return { ->
    println "jdbc:check: ${config.check}"
    newConnection(config).executeQuery "${config.check}"
  }
}

/**
 * @param config
 * @return
 * @since 0.1.0
 */
static Sql newConnection(Map config) {
  return Sql.newInstance(
    config.url,
    config.username,
    config.password,
    config.driver)
}
