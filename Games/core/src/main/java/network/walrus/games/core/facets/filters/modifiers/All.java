package network.walrus.games.core.facets.filters.modifiers;

import java.util.List;
import java.util.Map;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.utils.core.util.StringUtils;

/**
 * An all filter is a filter that will only pass if all of the children pass.
 *
 * @author Avicus Network
 */
public class All implements Filter {

  private final List<Filter> children;

  /**
   * Constructor.
   *
   * @param children that must all pass in order for the parent to pass
   */
  public All(List<Filter> children) {
    this.children = children;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Map<FilterResult, Integer> results = Filter.test(this.children, context, describe);
    int total = 0;
    for (Integer integer : results.values()) {
      int intValue = integer;
      total += intValue;
    }

    // allow if all allow
    if (results.get(FilterResult.ALLOW) == total) {
      return FilterResult.ALLOW;
    }

    // deny if at least one denies
    if (results.get(FilterResult.DENY) > 0) {
      return FilterResult.DENY;
    }

    // otherwise ignore
    return FilterResult.IGNORE;
  }

  public List<Filter> getChildren() {
    return children;
  }

  @Override
  public String describe() {
    return StringUtils.join(children, " AND ", Filter::describe);
  }
}
