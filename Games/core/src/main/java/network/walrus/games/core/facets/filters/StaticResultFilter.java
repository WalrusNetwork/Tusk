package network.walrus.games.core.facets.filters;

import network.walrus.games.core.facets.filters.context.FilterContext;

/**
 * A filter that will always return the same value.
 *
 * @author Avicus Network
 */
public class StaticResultFilter implements Filter {

  /** Result to always return */
  private final FilterResult result;

  /**
   * Constructor.
   *
   * @param result result to always return
   */
  public StaticResultFilter(FilterResult result) {
    this.result = result;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    return this.result;
  }

  @Override
  public String describe() {
    return "always " + result.name();
  }
}
