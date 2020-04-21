package network.walrus.games.core.facets.filters.modifiers;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;

/**
 * A deny filter will deny if the child filter passes.
 *
 * @author Avicus Network
 */
public class Deny implements Filter {

  private final Filter child;

  /**
   * Constructor.
   *
   * @param child which must return {@link FilterResult#DENY} for this to pass
   */
  public Deny(Filter child) {
    this.child = child;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    FilterResult result = child.test(context, describe);

    // deny if allow
    if (result == FilterResult.ALLOW) {
      return FilterResult.DENY;
    }

    // otherwise ignore
    return FilterResult.IGNORE;
  }

  public Filter getChild() {
    return child;
  }

  @Override
  public String describe() {
    return "DENY (" + child + ")";
  }
}
