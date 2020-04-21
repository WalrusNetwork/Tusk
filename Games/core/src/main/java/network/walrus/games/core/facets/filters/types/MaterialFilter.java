package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.MaterialVariable;
import network.walrus.utils.bukkit.inventory.MaterialMatcher;

/**
 * A material filter checks the type of material involved in an event.
 *
 * @author Avicus Network
 */
public class MaterialFilter implements Filter {

  private final MaterialMatcher matcher;

  /**
   * Constructor.
   *
   * @param matcher to be used for comparison
   */
  public MaterialFilter(MaterialMatcher matcher) {
    this.matcher = matcher;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<MaterialVariable> optional = context.getFirst(MaterialVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    boolean matches = this.matcher.matches(optional.get().getData());
    return FilterResult.valueOf(matches);
  }

  @Override
  public String describe() {
    return "material is " + matcher.toString();
  }
}
