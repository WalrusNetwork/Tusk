package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * The spawn reason variable contains information about the reason an entity was spawned.
 *
 * @author Avicus Network
 */
public class SpawnReasonVariable implements Variable {

  private final SpawnReason reason;

  /**
   * Constructor.
   *
   * @param reason that tbe creature is being spawned
   */
  public SpawnReasonVariable(SpawnReason reason) {
    this.reason = reason;
  }

  public SpawnReason getReason() {
    return reason;
  }
}
