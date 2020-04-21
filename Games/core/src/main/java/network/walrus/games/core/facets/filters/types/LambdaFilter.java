package network.walrus.games.core.facets.filters.types;

import java.util.function.Function;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;

/**
 * A lambda filter is just a simple filter that is written from the factory.
 *
 * @author Avicus Network
 */
public class LambdaFilter implements Filter {

  private final Function<FilterContext, FilterResult> filter;

  /**
   * Constructor.
   *
   * @param filter to execute during the test
   */
  public LambdaFilter(Function<FilterContext, FilterResult> filter) {
    this.filter = filter;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    return this.filter.apply(context);
  }

  @Override
  public String describe() {
    return "lambda";
  }
}
