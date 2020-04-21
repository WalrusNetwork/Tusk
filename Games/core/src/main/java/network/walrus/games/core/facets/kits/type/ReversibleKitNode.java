package network.walrus.games.core.facets.kits.type;

import java.util.List;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.ReversibleKit;
import org.bukkit.entity.Player;

/**
 * Represents a collection of reversible kits.
 *
 * @author Rafi Baum
 */
public class ReversibleKitNode extends ReversibleKit {

  private final List<ReversibleKit> kits;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param kits that make up this node
   */
  public ReversibleKitNode(
      boolean force, @Nullable ReversibleKit parent, List<ReversibleKit> kits) {
    super(force, parent);
    this.kits = kits;
  }

  @Override
  public void give(Player player, boolean force) {
    for (ReversibleKit kit : kits) {
      kit.give(player, force);
    }
  }

  @Override
  public void remove(Player player) {
    for (ReversibleKit kit : kits) {
      kit.remove(player);
    }
  }
}
