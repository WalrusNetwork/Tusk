package network.walrus.games.core.facets.filters.types.random;

import java.util.Random;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;

/**
 * A random filter randomly passes based on the supplied amount of randomness.
 *
 * @author Avicus Network
 */
public class RandomFilter implements Filter {

  private static final Random random = new Random();
  private final double value;

  /**
   * Constructor
   *
   * @param value to check against the random
   */
  public RandomFilter(double value) {
    this.value = value;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    return FilterResult.valueOf(random.nextDouble() <= this.value);
  }

  @Override
  public String describe() {
    return "random";
  }
}
