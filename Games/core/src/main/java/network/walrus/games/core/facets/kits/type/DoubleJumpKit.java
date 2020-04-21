package network.walrus.games.core.facets.kits.type;

import java.time.Duration;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.doublejump.DoubleJumpManager;
import org.bukkit.entity.Player;

/**
 * Kit used to give the player the ability to double jump
 *
 * @author David Rodriguez
 */
public class DoubleJumpKit extends Kit {

  private final boolean enabled;
  private final int power;
  private final Duration rechargeTime;
  private final boolean rechargeBeforeLanding;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param enabled if double jump is enabled
   * @param power used to modify the double jump's power
   * @param rechargeTime used to modify the recharge time
   * @param rechargeBeforeLanding if to schedule recharge before landing
   */
  public DoubleJumpKit(
      boolean force,
      @Nullable Kit parent,
      boolean enabled,
      int power,
      Duration rechargeTime,
      boolean rechargeBeforeLanding) {
    super(force, parent);
    this.enabled = enabled;
    this.power = power;
    this.rechargeTime = rechargeTime;
    this.rechargeBeforeLanding = rechargeBeforeLanding;
  }

  @Override
  public void give(Player player, boolean force) {
    DoubleJumpManager doubleJumpManager =
        UbiquitousBukkitPlugin.getInstance().getDoubleJumpManager();

    if (enabled) {
      doubleJumpManager.enableDoubleJump(player, power, rechargeTime, rechargeBeforeLanding);
    } else {
      doubleJumpManager.disableDoubleJump(player);
    }
  }
}
