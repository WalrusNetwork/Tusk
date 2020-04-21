package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * The damage variable that contains the type of damage inflicted on a player.
 *
 * @author Avicus Network
 */
public class DamageVariable implements Variable {

  private final DamageCause cause;

  /**
   * Constructor.
   *
   * @param cause of the damage
   */
  public DamageVariable(DamageCause cause) {
    this.cause = cause;
  }

  public DamageCause getCause() {
    return cause;
  }
}
