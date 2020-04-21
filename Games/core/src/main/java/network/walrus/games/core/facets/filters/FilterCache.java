package network.walrus.games.core.facets.filters;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A wrapper around a {@link Cache} that is used to cache filter results for a pre-defined period of
 * time.
 *
 * @param <K> type of key to get results by
 * @author Austin Mayes
 */
public class FilterCache<K> {

  private final Cache<K, FilterResult> cache;

  /**
   * Constructor.
   *
   * @param invalidationInterval time before the cache is invalidated after write
   */
  public FilterCache(int invalidationInterval) {
    cache =
        CacheBuilder.newBuilder().expireAfterWrite(invalidationInterval, TimeUnit.SECONDS).build();
  }

  /** Cache of 1 second */
  public FilterCache() {
    this(1);
  }

  /**
   * Get a value from the cache, computing it if it is not cached
   *
   * @param key to get the result by
   * @param compute function used to compute the result if it is not cached
   * @return the result for the key
   */
  public FilterResult get(K key, Function<K, FilterResult> compute) {
    FilterResult current = cache.getIfPresent(key);
    if (current == null) {
      current = compute.apply(key);
      cache(key, current);
    }
    return current;
  }

  /**
   * Determine if their is a cached result for the specified key
   *
   * @param key to search for
   * @return if the key is cached
   */
  public boolean isCached(K key) {
    return cache.getIfPresent(key) != null;
  }

  /**
   * Cache a value for a key.
   *
   * @param key to cache the value by
   * @param result to cache
   */
  public void cache(K key, FilterResult result) {
    cache.put(key, result);
  }
}
