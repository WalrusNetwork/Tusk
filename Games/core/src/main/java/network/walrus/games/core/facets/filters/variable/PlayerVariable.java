package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import org.bukkit.entity.Player;

/**
 * The player variable contains information about the player who is performing a checked action.
 * This holds all information about a player but is never used to find a specific player by name.
 *
 * @author Avicus Network
 */
public class PlayerVariable implements Variable {

  private final Player player;

  /**
   * Constructor.
   *
   * @param player the variable is for
   */
  public PlayerVariable(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }
}
