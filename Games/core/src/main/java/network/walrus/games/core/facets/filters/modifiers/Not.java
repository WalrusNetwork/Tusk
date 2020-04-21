package network.walrus.games.core.facets.filters.modifiers;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;

/**
 * A not filter inverts the result of it's child filter, unless it is ignored.
 *
 * @author Avicus Network
 */
public class Not implements Filter {

  private final Filter child;

  /**
   * Constructor.
   *
   * @param child who's result should be inverted
   */
  public Not(Filter child) {
    this.child = child;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    FilterResult result = child.test(context, describe);

    // ignore if ignore
    if (result == FilterResult.IGNORE) {
      return FilterResult.IGNORE;
    }

    // otherwise invert
    return result == FilterResult.ALLOW ? FilterResult.DENY : FilterResult.ALLOW;
  }

  public Filter getChild() {
    return child;
  }

  @Override
  public String describe() {
    return "NOT (" + child.describe() + ")";
  }
}
