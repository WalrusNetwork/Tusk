package network.walrus.nerve.core.redis;

import com.lambdaworks.redis.RedisURI;
import java.util.Optional;

/**
 * Configuration class for the redis connection.
 *
 * @author Austin Mayes
 */
public class RedisConfig {

  private final String host;
  private final int port;
  private final Optional<String> password;
  private final int db;
  private final int threads;

  /**
   * Constructor.
   *
   * @param host of the Redis server
   * @param port of the Redis server
   * @param password of the Redis server
   * @param db to cache data in
   * @param threads to use for message handling
   */
  public RedisConfig(String host, int port, Optional<String> password, int db, int threads) {
    this.host = host;
    this.port = port;
    this.password = password;
    this.db = db;
    this.threads = threads;
  }

  public int getThreads() {
    return threads;
  }

  /** @return a {@link RedisURI} populatated with data from this config */
  public RedisURI constructURI() {
    RedisURI.Builder uri =
        RedisURI.builder().withDatabase(this.db).withHost(this.host).withPort(this.port);
    this.password.ifPresent(uri::withPassword);
    return uri.build();
  }
}
