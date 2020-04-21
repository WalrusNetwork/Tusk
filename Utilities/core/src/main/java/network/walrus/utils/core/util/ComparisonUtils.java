package network.walrus.utils.core.util;

import java.util.function.Predicate;

/**
 * General utility methods which don't really fit anywhere else.
 *
 * @author Overcast Network
 */
public class ComparisonUtils {

  /**
   * Determine if {@code self} and {@code that} are equal in terms of a basic equality search of
   * that {@link T} is assignable to that and the supplied predicate returns {@code true}.
   *
   * @param type type of self object
   * @param self to compare against that
   * @param that to compare self against
   * @param test to run for comparison between self and a casted version of that to {@link T}
   * @param <T> type of self
   * @return if this and that are equal
   */
  public static <T> boolean equals(Class<T> type, T self, Object that, Predicate<T> test) {
    return self == that || (type.isInstance(that) && test.test(type.cast(that)));
  }
}
