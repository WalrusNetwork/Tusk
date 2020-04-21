package network.walrus.utils.core.math;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;

/**
 * Utilities for working with {@link Range}s.
 *
 * @author Overcast Network
 */
public class RangeUtils {

  private RangeUtils() {}

  /**
   * Ensure a range has a lower bound defined.
   *
   * @param range to check
   */
  public static void assertLowerBound(Range<?> range) {
    if (!range.hasLowerBound()) {
      throw new IllegalArgumentException("Range has no lower bound");
    }
  }

  /**
   * Ensure a range has an upper bound defined.
   *
   * @param range to check
   */
  public static void assertUpperBound(Range<?> range) {
    if (!range.hasLowerBound()) {
      throw new IllegalArgumentException("Range has no upper bound");
    }
  }

  /**
   * Ensure a range has a lower and an upper bound defined.
   *
   * @param range to check
   */
  public static void assertBounded(Range<?> range) {
    assertLowerBound(range);
    assertUpperBound(range);
  }

  /**
   * Get the minimum number allowed by this range, if the range has a lower bound.
   *
   * @param range to get min value from
   * @return minimum number allowed by this range
   */
  public static Optional<Integer> minimum(Range<Integer> range) {
    return range.hasLowerBound()
        ? Optional.of(
            range.lowerBoundType() == BoundType.CLOSED
                ? range.lowerEndpoint()
                : Integer.valueOf(range.lowerEndpoint() + 1))
        : Optional.empty();
  }

  /**
   * Get the maximum number allowed by this range, if the range has an upper bound.
   *
   * @param range to get max value from
   * @return maximum number allowed by this range
   */
  public static Optional<Integer> maximum(Range<Integer> range) {
    return range.hasUpperBound()
        ? Optional.of(
            range.upperBoundType() == BoundType.CLOSED
                ? range.upperEndpoint()
                : Integer.valueOf(range.upperEndpoint() - 1))
        : Optional.empty();
  }

  /** Combination of {@link #assertLowerBound(Range)} and {@link #minimum(Range)}. */
  public static int needMinimum(Range<Integer> range) {
    assertLowerBound(range);
    return range.lowerBoundType() == BoundType.CLOSED
        ? range.lowerEndpoint()
        : range.lowerEndpoint() + 1;
  }

  /**
   * Always return the open minimum of a range, regardless of the range's {@link BoundType}.
   *
   * @param range to get minimum for
   * @return open minimum of the range
   */
  public static int needOpenMinimum(Range<Integer> range) {
    assertLowerBound(range);
    return range.lowerBoundType() == BoundType.CLOSED
        ? range.lowerEndpoint() - 1
        : range.lowerEndpoint();
  }

  /** Combination of {@link #assertUpperBound(Range)} and {@link #maximum(Range)}. */
  public static int needMaximum(Range<Integer> range) {
    assertUpperBound(range);
    return range.upperBoundType() == BoundType.CLOSED
        ? range.upperEndpoint()
        : range.upperEndpoint() - 1;
  }

  /**
   * Always return the open maximum of a range, regardless of the range's {@link BoundType}.
   *
   * @param range to get maximum for
   * @return open maximum of the range
   */
  public static int needOpenMaximum(Range<Integer> range) {
    assertUpperBound(range);
    return range.upperBoundType() == BoundType.CLOSED
        ? range.upperEndpoint() + 1
        : range.upperEndpoint();
  }

  /**
   * Convert an open bounded range to a closed one, or return it unchanged if it is already closed.
   *
   * @param range to convert
   * @return closed version of the range
   */
  public static Range<Integer> toClosed(Range<Integer> range) {
    assertBounded(range);
    return range.lowerBoundType() == BoundType.CLOSED && range.upperBoundType() == BoundType.CLOSED
        ? range
        : Range.closed(needMinimum(range), needMaximum(range));
  }

  /**
   * Execute an operation on all of the numbers defined by a bounded range.
   *
   * @param range of numbers to be acted on
   * @param consumer to run on each number
   */
  public static void forEach(Range<Integer> range, IntConsumer consumer) {
    final int max = needOpenMaximum(range);
    for (int i = needMinimum(range); i < max; i++) {
      consumer.accept(i);
    }
  }

  /**
   * Return an english phrase describing the given {@link Range} e.g.
   *
   * <p>Range.all() -> "unbounded" Range.singleton(3) -> "3" Range.atLeast(3) -> "at least 3"
   * Range.closedOpen(3, 7) -> "at least 3 and less than 7" Range.closed(3, 7) -> "between 3 and 7"
   */
  public static String describe(Range<?> range) {
    if (range.hasLowerBound()
        && range.hasUpperBound()
        && range.lowerBoundType() == BoundType.CLOSED
        && range.upperBoundType() == BoundType.CLOSED) {
      if (range.lowerEndpoint().equals(range.upperEndpoint())) {
        // singleton
        return range.lowerEndpoint().toString();
      } else {
        // closed-closed
        return "between " + range.lowerEndpoint() + " and " + range.upperEndpoint();
      }
    }

    final List<String> parts = new ArrayList<>(2);

    if (range.hasLowerBound()) {
      parts.add(
          (range.lowerBoundType() == BoundType.CLOSED ? "at least " : "more than ")
              + range.lowerEndpoint());
    }

    if (range.hasUpperBound()) {
      parts.add(
          (range.upperBoundType() == BoundType.CLOSED ? "at most " : "less than ")
              + range.upperEndpoint());
    }

    switch (parts.size()) {
      case 0:
        return "unbounded";
      case 1:
        return parts.get(0);
      default:
        return parts.get(0) + " and " + parts.get(1);
    }
  }
}
