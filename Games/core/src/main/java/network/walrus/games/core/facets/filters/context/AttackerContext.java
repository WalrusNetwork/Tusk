package network.walrus.games.core.facets.filters.context;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.variable.AttackerVariable;

/**
 * An attacker filter is a wrapper filter that contains player information about the cause of an
 * attack.
 *
 * @author Avicus Network
 */
public class AttackerContext implements Filter {

  private final Filter filter;

  /**
   * Constructor.
   *
   * @param filter that will be ran of the context contains an attacker variable
   */
  public AttackerContext(Filter filter) {
    this.filter = filter;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<AttackerVariable> attacker = context.getFirst(AttackerVariable.class);

    if (!attacker.isPresent()) {
      return FilterResult.IGNORE;
    }

    return this.filter.test(attacker.get(), describe);
  }

  @Override
  public String describe() {
    return "attacker " + filter.describe();
  }
}
