package network.walrus.games.core.facets.filters.modifiers;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;

/**
 * An allow filter will only pass if the child filter is not ignored and passes.
 *
 * @author Avicus Network
 */
public class Allow implements Filter {

  private final Filter child;

  /**
   * Constructor.
   *
   * @param child which must return {@link FilterResult#ALLOW}
   */
  public Allow(Filter child) {
    this.child = child;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    FilterResult result = child.test(context, describe);

    // allow if allow
    if (result == FilterResult.ALLOW) {
      return FilterResult.ALLOW;
    }

    // otherwise ignore
    return FilterResult.IGNORE;
  }

  public Filter getChild() {
    return child;
  }

  @Override
  public String describe() {
    return "ALLOW (" + child + ")";
  }
}
