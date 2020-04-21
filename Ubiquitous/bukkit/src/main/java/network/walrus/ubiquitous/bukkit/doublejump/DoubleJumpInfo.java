package network.walrus.ubiquitous.bukkit.doublejump;

import java.time.Duration;
import network.walrus.utils.bukkit.cooldown.CooldownTracker;
import org.bukkit.entity.Player;

/**
 * Class containing information that was passed to the {@link
 * DoubleJumpManager#enableDoubleJump(Player, int, Duration, boolean)} method
 *
 * @author David Rodriguez
 */
class DoubleJumpInfo {
  private final CooldownTracker cooldownTracker;
  private final int power;
  private final Duration rechargeTime;
  private final boolean rechargeBeforeLanding;
  private final float chargePerTick;

  DoubleJumpInfo(Player player, int power, Duration rechargeTime, boolean rechargeBeforeLanding) {
    this.cooldownTracker = new CooldownTracker(player);
    this.power = power;
    this.rechargeTime = rechargeTime;
    this.rechargeBeforeLanding = rechargeBeforeLanding;
    this.chargePerTick = 50F / rechargeTime.toMillis();
  }

  CooldownTracker getCooldownTracker() {
    return cooldownTracker;
  }

  int getPower() {
    return power;
  }

  Duration getRechargeTime() {
    return rechargeTime;
  }

  boolean rechargeBeforeLanding() {
    return rechargeBeforeLanding;
  }

  float getChargePerTick() {
    return chargePerTick;
  }
}
