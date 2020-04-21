package network.walrus.nerve.core.redis;

import com.google.gson.Gson;
import com.lambdaworks.redis.api.sync.RedisCommands;
import network.walrus.nerve.core.api.exception.ApiException;

/**
 * Abstract implementation of {@link RedisService} with helper methods for accessing redis
 * resources.
 *
 * @param <D> type of document the service is responsible for
 * @author Austin Mayes
 */
public abstract class AbstractRedisService<D> implements RedisService<D> {

  private final Gson gson = RedisClient.gson();

  /**
   * Provides a {@link RedisClient} to all Service Implementation
   *
   * @return single {@link RedisClient} for all services.
   * @throws ApiException if the client is not configured
   */
  protected RedisClient client() throws ApiException {
    return RedisClient.getInstance();
  }

  @Override
  public RedisCommands<String, String> commands() throws ApiException {
    return client().getRedisCommands();
  }

  @Override
  public void publish(String channel, D document) throws ApiException {
    client().getRedisCommands().publish(channel, gson.toJson(document));
  }
}
