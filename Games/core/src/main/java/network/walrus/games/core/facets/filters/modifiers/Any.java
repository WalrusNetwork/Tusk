package network.walrus.games.core.facets.filters.modifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.utils.core.util.StringUtils;

/**
 * An any filter will pass if any of it's children pass.
 *
 * @author Avicus Network
 */
public class Any implements Filter {

  private final List<Filter> children;

  /**
   * Constructor.
   *
   * @param children of this filter
   */
  public Any(List<Filter> children) {
    this.children = children;
  }

  /** @see #Any(List). */
  public Any(Filter... children) {
    this(Arrays.asList(children));
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Map<FilterResult, Integer> results = Filter.test(this.children, context, describe);

    // allow if one child allows
    if (results.get(FilterResult.ALLOW) > 0) {
      return FilterResult.ALLOW;
    }

    // deny if none allow, more than one deny
    else if (results.get(FilterResult.ALLOW) == 0 && results.get(FilterResult.DENY) > 0) {
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
    return StringUtils.join(children, " OR ", Filter::describe);
  }
}
