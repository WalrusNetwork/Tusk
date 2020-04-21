package network.walrus.games.core.facets.kits.type;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.core.math.PreparedNumberAction;
import org.bukkit.entity.Player;

/**
 * Kit used to modify movement attributes for a player
 *
 * @author Avicus Network
 */
public class MovementKit extends Kit {

  private final PreparedNumberAction exhaustion;
  private final PreparedNumberAction flySpeed;
  private final PreparedNumberAction walkSpeed;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param exhaustion used to modify the player's exhaustion
   * @param flySpeed used to modify the player's flight speed
   * @param walkSpeed used to modify the player's walking speed
   */
  public MovementKit(
      boolean force,
      @Nullable Kit parent,
      PreparedNumberAction exhaustion,
      PreparedNumberAction flySpeed,
      PreparedNumberAction walkSpeed) {
    super(force, parent);
    this.exhaustion = exhaustion;
    this.flySpeed = flySpeed;
    this.walkSpeed = walkSpeed;
  }

  @Override
  public void give(Player player, boolean force) {
    // Movement
    if (this.exhaustion != null) {
      player.setExhaustion(this.exhaustion.perform(player.getExhaustion()));
    }
    if (this.flySpeed != null) {
      player.setFlySpeed(this.flySpeed.perform(player.getFlySpeed()));
    }
    if (this.walkSpeed != null) {
      player.setWalkSpeed(this.walkSpeed.perform(player.getWalkSpeed()));
    }
  }
}
