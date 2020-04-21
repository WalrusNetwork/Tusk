package network.walrus.utils.core.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import network.walrus.utils.core.lambda.ThrowingFunction;

/**
 * Useful utilities for interacting with {@link com.google.common.cache.Cache}s.
 *
 * @author Overcast Network
 */
public interface CacheUtils {

  /**
   * Create a simple {@link CacheBuilder} with a {@link K} and {@link V} cast.
   *
   * @param <K> key type
   * @param <V> value type
   * @return {@link CacheBuilder#newBuilder()}
   */
  static <K, V> CacheBuilder<K, V> newBuilder() {
    return (CacheBuilder<K, V>) CacheBuilder.newBuilder();
  }

  /**
   * Create a new {@link com.google.common.cache.Cache} using the supplied {@link CacheBuilder}
   * which will execute the {@link ThrowingFunction} for value generation.
   *
   * @param builder used to build the cache
   * @param loader which is called to load values
   * @param <K> key type
   * @param <V> value type
   * @param <E> exception that value generation can throw
   * @return a cache which executes the function to generate keys
   */
  static <K, V, E extends Exception> LoadingCache<K, V> newCache(
      CacheBuilder<K, V> builder, ThrowingFunction<K, V, E> loader) {
    return builder.build(
        new CacheLoader<K, V>() {
          @Override
          public V load(K key) throws E {
            return loader.applyThrows(key);
          }
        });
  }

  /** @see #newCache(CacheBuilder, ThrowingFunction) */
  static <K, V, E extends Exception> LoadingCache<K, V> newCache(ThrowingFunction<K, V, E> loader) {
    return newCache(newBuilder(), loader);
  }
}
