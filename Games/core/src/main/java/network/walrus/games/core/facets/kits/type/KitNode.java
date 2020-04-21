package network.walrus.games.core.facets.kits.type;

import java.util.List;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import org.bukkit.entity.Player;

/**
 * A collection of various {@link Kit} types which can be applied to a player in unison.
 *
 * @author Avicus Network
 */
public class KitNode extends Kit {

  private final List<Kit> kits;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param kits that make up this node
   */
  public KitNode(boolean force, @Nullable Kit parent, List<Kit> kits) {
    super(force, parent);
    this.kits = kits;
  }

  @Override
  public void give(Player player, boolean force) {
    for (Kit l : this.kits) {
      l.give(player, force);
    }
  }
}
