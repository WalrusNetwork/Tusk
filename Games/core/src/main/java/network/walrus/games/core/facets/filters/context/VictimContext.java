package network.walrus.games.core.facets.filters.context;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.variable.VictimVariable;

/**
 * A victim filter is a wrapper filter that contains player information about the victim of an
 * attack.
 *
 * @author Avicus Network
 */
public class VictimContext implements Filter {

  private final Filter filter;

  /**
   * Constructor.
   *
   * @param filter to be ran on the victim if the victim exists in the context
   */
  public VictimContext(Filter filter) {
    this.filter = filter;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<VictimVariable> victim = context.getFirst(VictimVariable.class);

    if (!victim.isPresent()) {
      return FilterResult.IGNORE;
    }

    return this.filter.test(victim.get(), describe);
  }

  @Override
  public String describe() {
    return "victim " + filter.describe();
  }
}
