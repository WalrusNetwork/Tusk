package network.walrus.games.core.facets.kits;

import javax.annotation.Nullable;
import org.bukkit.entity.Player;

/**
 * Kit which can have its effects reversed.
 *
 * @author Rafi Baum
 */
public abstract class ReversibleKit extends Kit {

  private final @Nullable Kit parent;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   */
  public ReversibleKit(boolean force, @Nullable Kit parent) {
    super(force, parent);
    this.parent = parent;
  }

  public void unapply(Player player) {
    if (this.parent != null) {
      if (this.parent instanceof ReversibleKit) {
        ((ReversibleKit) this.parent).unapply(player);
      } else {
        throw new IllegalStateException("Tried to reverse kit which has irreversible parent!");
      }
    }

    remove(player);
  }

  public abstract void remove(Player player);
}
