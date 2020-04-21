package network.walrus.nerve.core.redis;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * Provides the correct Service type for different redis documents.
 *
 * @author Austin Mayes
 */
public class RedisServiceProvider {

  private static Map<Class<?>, RedisService<?>> SERVICE_MAP = Maps.newHashMap();

  /**
   * Register a service to a specific class.
   *
   * @param clazz which the service is handling
   * @param service which is interacting with Redis
   * @param <D> type of document the service handles
   */
  public static <D> void register(Class<D> clazz, RedisService<D> service) {
    SERVICE_MAP.put(clazz, service);
  }

  /**
   * Get the service which should be used for interacting with documents of type {@link A}.
   *
   * @param clazz of the document to get the service for
   * @param <D> type of document being retrieved
   * @return service for the supplied document
   */
  @SuppressWarnings("unchecked")
  public static <D> RedisService<D> getService(Class<D> clazz) {
    return (RedisService<D>) SERVICE_MAP.get(clazz);
  }
}
