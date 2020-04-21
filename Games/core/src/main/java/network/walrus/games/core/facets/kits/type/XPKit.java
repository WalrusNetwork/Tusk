package network.walrus.games.core.facets.kits.type;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.core.math.PreparedNumberAction;
import org.bukkit.entity.Player;

/**
 * Kit which gives XP and levels to players based on number actions.
 *
 * @author Avicus Network
 */
public class XPKit extends Kit {

  private final PreparedNumberAction level;
  private final PreparedNumberAction exp;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param level used to set player levels
   * @param exp used to set player XP
   */
  public XPKit(
      boolean force, @Nullable Kit parent, PreparedNumberAction level, PreparedNumberAction exp) {
    super(force, parent);
    this.level = level;
    this.exp = exp;
  }

  @Override
  public void give(Player player, boolean force) {
    // Experience
    if (this.level != null) {
      player.setLevel(this.level.perform(player.getLevel()));
    }
    if (this.exp != null) {
      player.setExp(this.exp.perform(player.getExp()));
    }
  }
}
