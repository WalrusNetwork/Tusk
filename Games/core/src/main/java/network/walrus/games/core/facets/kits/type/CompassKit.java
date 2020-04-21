package network.walrus.games.core.facets.kits.type;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Kit used to set a player's compass target.
 *
 * @author Avicus Network
 */
public class CompassKit extends Kit {

  private final Vector target;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param target to point the player's compass to
   */
  public CompassKit(boolean force, @Nullable Kit parent, Vector target) {
    super(force, parent);
    this.target = target;
  }

  @Override
  public void give(Player player, boolean force) {
    player.setCompassTarget(this.target.toLocation(player.getWorld()));
  }
}
