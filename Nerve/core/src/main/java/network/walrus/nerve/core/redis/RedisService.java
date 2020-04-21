package network.walrus.nerve.core.redis;

import com.lambdaworks.redis.api.sync.RedisCommands;
import network.walrus.nerve.core.api.exception.ApiException;

/**
 * Service responsible for the publication and retrieval of {@link D}s to/from Redis.
 *
 * @param <D> type of document the service is responsible for
 * @author Austin Mayes
 */
public interface RedisService<D> {

  /**
   * @return commands used to interact with Redis
   * @throws ApiException if Redis is not connected
   */
  RedisCommands<String, String> commands() throws ApiException;

  /**
   * Publish a document to the specified channel.
   *
   * @param channel to publish the document to
   * @param document to publish
   * @throws ApiException if Redis is not connected
   */
  void publish(String channel, D document) throws ApiException;
}
