package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import org.bukkit.entity.Entity;

/**
 * THe entity variable contains information about the type of entity that a filter is being
 * performed against.
 *
 * @author Avicus Network
 */
public class EntityVariable implements Variable {

  private final Entity entity;

  /**
   * Constructor.
   *
   * @param entity which filters can query against
   */
  public EntityVariable(Entity entity) {
    this.entity = entity;
  }

  public Entity getEntity() {
    return entity;
  }
}
