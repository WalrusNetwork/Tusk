package network.walrus.nerve.core.redis;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.api.sync.RedisPubSubCommands;
import com.shopify.graphql.support.ID;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import network.walrus.nerve.core.api.exception.ApiException;
import network.walrus.nerve.core.api.exception.RedisException;
import network.walrus.nerve.core.redis.adapters.IDAdapter;

/**
 * Holder of the main Redis connection for this instance.
 *
 * @author Austin Mayes
 */
public class RedisClient {

  public static Gson gson;
  private static RedisClient instance = null;
  private static Map<Type, TypeAdapter> adapters = Maps.newHashMap();
  private final ListeningExecutorService executor;
  private final com.lambdaworks.redis.RedisClient client;
  private final Set<RedisListener> listeners = Sets.newHashSet();
  private RedisCommands<String, String> redisCommands;
  private RedisPubSubCommands<String, String> pubSubCommands;

  /**
   * Constructor.
   *
   * @param config to apply to the client.
   */
  private RedisClient(RedisConfig config) {
    this.executor =
        MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(config.getThreads()));
    this.client = com.lambdaworks.redis.RedisClient.create(config.constructURI());
    registerTypeAdapter(ID.class, new IDAdapter());
  }

  /** @return the gson instance which should be used for (de)serialization */
  public static Gson gson() {
    if (gson == null) {
      GsonBuilder builder = new GsonBuilder();
      for (Entry<Type, TypeAdapter> entry : adapters.entrySet()) {
        Type key = entry.getKey();
        TypeAdapter value = entry.getValue();
        builder.registerTypeAdapter(key, value);
      }
      gson = builder.create();
    }
    return gson;
  }

  /**
   * Register an adapter which will be used to convert custom types during (de)serialization.
   *
   * @param type that the adapter is for
   * @param adapter to register
   */
  public static void registerTypeAdapter(Type type, TypeAdapter adapter) {
    if (gson != null)
      throw new IllegalStateException(
          "Cannot register an adapter after the gson instance has been constructed");

    adapters.put(type, adapter);
  }

  /**
   * Configure the client.
   *
   * @param config to apply to the client.
   * @return the initialized client
   */
  public static RedisClient initialize(RedisConfig config) {
    instance = new RedisClient(config);
    return instance;
  }

  /**
   * @return the global client instance
   * @throws ApiException if the client is not configured
   */
  public static RedisClient getInstance() throws ApiException {
    if (instance == null) {
      throw new ApiException("RedisClient not configured!");
    }
    return instance;
  }

  /**
   * Register a listener which will receive updates for every channel defined in {@link
   * RedisListener#channels()}.
   *
   * @param listener to register
   */
  public void registerListener(RedisListener listener) {
    listeners.add(listener);
    this.pubSubCommands.subscribe(listener.channels());
  }

  /** Connect to Redis. */
  public void connect() {
    this.pubSubCommands = this.client.connectPubSub().sync();
    this.redisCommands = this.client.connect().sync();

    this.pubSubCommands.getStatefulConnection().addListener(new Listener());
  }

  /** Close the Redis connection. */
  public void disconnect() {
    if (redisCommands == null) {
      return;
    }

    this.pubSubCommands.getStatefulConnection().close();
    this.redisCommands.getStatefulConnection().close();
    this.client.shutdown();
  }

  RedisCommands<String, String> getRedisCommands() {
    return redisCommands;
  }

  public RedisPubSubCommands<String, String> getPubSubCommands() {
    return pubSubCommands;
  }

  private class Listener implements RedisPubSubListener<String, String> {

    private final Gson gson = gson();

    @Override
    public void message(String channel, String message) {
      StackTraceElement[] elements = new Exception().getStackTrace();
      listeners.stream()
          .filter(l -> l.matches(channel))
          .forEach(
              l -> {
                executor.submit(
                    () -> {
                      try {
                        l.handle(gson.fromJson(message, l.type()));
                      } catch (Exception e) {
                        RedisException exception =
                            new RedisException(
                                "Exception thrown when handling input to " + l.getClass(),
                                e,
                                channel,
                                message);
                        exception.setStackTrace(elements);
                        System.out.println("Channel: " + channel);
                        System.out.println("Message: " + message);
                        exception.printStackTrace();
                      }
                    });
              });
    }

    @Override
    public void message(String pattern, String channel, String message) {}

    @Override
    public void subscribed(String channel, long count) {}

    @Override
    public void psubscribed(String pattern, long count) {}

    @Override
    public void unsubscribed(String channel, long count) {}

    @Override
    public void punsubscribed(String pattern, long count) {}
  }
}
