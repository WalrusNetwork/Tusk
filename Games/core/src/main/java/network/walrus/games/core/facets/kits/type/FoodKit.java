package network.walrus.games.core.facets.kits.type;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.core.math.PreparedNumberAction;
import org.bukkit.entity.Player;

/**
 * Kit used to modify a player's food attributes.
 *
 * @author Avicus Network
 */
public class FoodKit extends Kit {

  private final PreparedNumberAction foodLevel;
  private final PreparedNumberAction saturation;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param foodLevel used to modify the player's food level
   * @param saturation used to modify the player's saturation level
   */
  public FoodKit(
      boolean force,
      @Nullable Kit parent,
      PreparedNumberAction foodLevel,
      PreparedNumberAction saturation) {
    super(force, parent);
    this.foodLevel = foodLevel;
    this.saturation = saturation;
  }

  @Override
  public void give(Player player, boolean force) {
    // Food
    if (this.foodLevel != null) {
      player.setFoodLevel(this.foodLevel.perform(player.getFoodLevel()));
    }
    if (this.saturation != null) {
      player.setSaturation(this.saturation.perform(player.getSaturation()));
    }
  }
}
