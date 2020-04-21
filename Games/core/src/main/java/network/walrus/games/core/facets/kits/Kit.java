package network.walrus.games.core.facets.kits;

import javax.annotation.Nullable;
import org.bukkit.entity.Player;

/**
 * An object which can be applied to a player at any point in time.
 *
 * @author Avicus Network
 */
public abstract class Kit {

  private final boolean force;
  private final @Nullable Kit parent;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   */
  public Kit(boolean force, @Nullable Kit parent) {
    this.force = force;
    this.parent = parent;
  }

  /**
   * Apply this kit to a specific player.
   *
   * @param player to apply the kit to
   * @param force if items should be overridden
   */
  public void apply(Player player, boolean force) {
    if (this.parent != null) {
      this.parent.apply(player, force);
    }
    give(player, force);
  }

  /** @see #apply(Player, boolean) */
  public void apply(Player player) {
    apply(player, false);
  }

  /**
   * Give this specific kit to a player, ignoring parents.
   *
   * @param player the kit is being given to
   * @param force if items should be overridden
   */
  public abstract void give(Player player, boolean force);

  public boolean isForce() {
    return force;
  }
}
