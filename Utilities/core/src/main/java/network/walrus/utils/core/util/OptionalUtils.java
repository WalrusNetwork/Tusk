package network.walrus.utils.core.util;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Useful utilities for working with {@link Optional}s.
 *
 * @author Overcast Network
 */
public final class OptionalUtils {

  private OptionalUtils() {}

  /**
   * Get the first present option in a stream of optionals.
   *
   * @param options to iterate over
   * @param <T> type of option
   * @return the first present option
   */
  public static <T> Optional<T> first(Stream<Optional<? extends T>> options) {
    return (Optional<T>) options.filter(Optional::isPresent).findFirst().orElse(Optional.empty());
  }

  /** @see #first(Stream). */
  public static <T> Optional<T> first(Optional<? extends T>... options) {
    return first(Stream.of(options));
  }
}
