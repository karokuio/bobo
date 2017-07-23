/**
 * RabbitMQ
 * ========
 *
 * This watcher checks that the defined RabbitMQ connection
 * is ready for accepting new requests.
 *
 * For the time being the only check is to be able to create
 * an anonymous queue in the RabbitMQ instance
 *
 * @author @marioggar
 * @since 0.1.0
 *
 */
package bobo

import java.util.function.Supplier
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import io.github.resilience4j.retry.Retry

import bobo.common as C

/**
 * Executes the watcher with the parameters defined in the services.yml
 * configuration file
 *
 * @param config watcher configuration, coming from the services.yml
 * file
 * @since 0.1.0
 */
static void watch(Map config) {
  String watcherId = "rabbit:${config.name}"
  Retry retry = C.newRetry(watcherId, config)

  retry.executeSupplier(newQueueChecker(config))
}

/**
 * Creates a supplier checking whether we can or we can't create a
 * queue in the configured RabbitMQ instance
 *
 * @param config RabbitMQ configuration
 * @return an instance of {@link Supplier} which executes the
 * condition
 * @since 0.1.0
 */
static Supplier newQueueChecker(Map config) {
  return { ->
    println "rabbit:check:queue"
    ConnectionFactory factory = new ConnectionFactory()
    factory.setUsername(config.username)
    factory.setPassword(config.password)
    factory.setHost(config.host)
    factory.setPort(config.port)

    Connection connection = factory.newConnection()
    Channel channel = connection.newChannel()

    channel.declareQueue()
  }
}
