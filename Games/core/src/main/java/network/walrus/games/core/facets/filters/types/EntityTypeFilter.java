package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.EntityVariable;
import org.bukkit.entity.EntityType;

/**
 * An entity type filter checks the type of entity that is causing an event.
 *
 * @author Avicus Network
 */
public class EntityTypeFilter implements Filter {

  private final EntityType type;

  /**
   * Constructor.
   *
   * @param type of entity this filter will match against
   */
  public EntityTypeFilter(EntityType type) {
    this.type = type;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<EntityVariable> var = context.getFirst(EntityVariable.class);
    if (var.isPresent()) {
      return FilterResult.valueOf(this.type == var.get().getEntity().getType());
    }
    return FilterResult.IGNORE;
  }

  @Override
  public String describe() {
    return "entity type is " + type.name();
  }
}
