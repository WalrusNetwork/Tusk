package network.walrus.ubiquitous.bukkit.doublejump;

import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.task.BetterRunnable;
import network.walrus.utils.bukkit.cooldown.CooldownTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

/**
 * Main class that keeps track of players that were given the DoubleJumpKit. This class keeps track
 * of all the players that were given the kit and can activate a cool down for the double jump
 *
 * @author David Rodriguez
 */
public class DoubleJumpManager {
  private final Map<UUID, DoubleJumpInfo> givenTo = Maps.newHashMap();
  private final DoubleJumpListener listener = new DoubleJumpListener(this);
  private Optional<BukkitTask> runningTask;

  /**
   * Start the double jump manager which starts a task that calls the {@link CooldownTracker#tick()}
   * method inside the {@link CooldownTracker}
   */
  public void enable() {
    BetterRunnable cooldownTask =
        () -> {
          for (Entry<UUID, DoubleJumpInfo> entry : givenTo.entrySet()) {
            UUID uuid = entry.getKey();
            DoubleJumpInfo doubleJumpInfo = entry.getValue();
            Player player = Bukkit.getPlayer(uuid);
            CooldownTracker cooldownTracker = doubleJumpInfo.getCooldownTracker();

            if (player == null) continue;
            if (cooldownTracker.isNotCooling(player)) continue;

            cooldownTracker.tick();
            setCharge(player, player.getExp() + doubleJumpInfo.getChargePerTick());
          }
        };
    runningTask = Optional.of(cooldownTask.runTaskTimer(0, 1, "cooldown-doublejump"));
    Bukkit.getPluginManager().registerEvents(listener, UbiquitousBukkitPlugin.getInstance());
  }

  /**
   * Disable the task that calls the {@link CooldownTracker#tick()} method inside the {@link
   * CooldownTracker}
   */
  public void disable() {
    runningTask.ifPresent(BukkitTask::cancel);
    givenTo.clear();
    HandlerList.unregisterAll(listener);
  }

  /**
   * Grants a player the ability to double jump
   *
   * @param player Target
   * @param power Power of the double jump
   * @param rechargeTime Time until the player can double jump again
   * @param rechargeBeforeLanding Player starts double jump recharge before landing
   */
  public void enableDoubleJump(
      Player player, int power, Duration rechargeTime, boolean rechargeBeforeLanding) {
    DoubleJumpInfo doubleJumpInfo =
        new DoubleJumpInfo(player, power, rechargeTime, rechargeBeforeLanding);
    givenTo.put(player.getUniqueId(), doubleJumpInfo);

    // Allow player to double jump
    player.setAllowFlight(true);
    setCharge(player, 1F);
  }

  /**
   * Takes away the ability to double jump from a player
   *
   * @param player Target
   */
  public void disableDoubleJump(Player player) {
    givenTo.remove(player.getUniqueId());
    player.setAllowFlight(false);
    setCharge(player, 0F);
  }

  /**
   * Checks if a target player has been given the double jump kit
   *
   * @param player Target player
   * @return if the player has the kit
   */
  public boolean hasKit(Player player) {
    return givenTo.containsKey(player.getUniqueId());
  }

  DoubleJumpInfo getPlayerInfo(Player player) {
    return givenTo.get(player.getUniqueId());
  }

  void setCharge(Player player, float charge) {
    float newCharge = Math.min(charge, 1f);
    player.setExp(newCharge);
  }

  void setCoolingDown(Player player) {
    DoubleJumpInfo doubleJumpInfo = getPlayerInfo(player);
    doubleJumpInfo.getCooldownTracker().coolFor(player, doubleJumpInfo.getRechargeTime());
  }
}
