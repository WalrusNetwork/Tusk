package network.walrus.ubiquitous.bukkit.freeze;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.task.BetterRunnable;
import network.walrus.utils.bukkit.NMSUtils.FakeArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

/**
 * Main backbone of the player freezing system. This class keeps track of all curently frozen
 * players and provides various convenience methods to interact with them or freeze new players.
 *
 * @author Austin Mayes
 */
public class FreezeManager {

  private final Map<Player, FakeArmorStand> frozenPlayers = Maps.newHashMap();
  private final BetterRunnable mounter =
      () -> {
        for (Entry<Player, FakeArmorStand> entry : new HashMap<>(frozenPlayers).entrySet()) {
          Player p = entry.getKey();
          FakeArmorStand s = entry.getValue();
          if (!p.isOnline()) {
            frozenPlayers.remove(p);
          }
          if (p.isInsideVehicle()) {
            p.eject();
          }
          s.mount(p, p);
        }
      };
  private final FreezeListener listener = new FreezeListener(this);
  private @Nullable BukkitTask runningMounter;

  /** Start the freeze mounter agent. */
  public void enable() {
    runningMounter = mounter.runTaskTimer(0, 10, "freeze-mounter");
    Bukkit.getPluginManager().registerEvents(listener, UbiquitousBukkitPlugin.getInstance());
  }

  /** Stop the freeze mounter agent and thaw all players. */
  public void disable() {
    if (runningMounter == null) {
      return;
    }
    runningMounter.cancel();
    for (Entry<Player, FakeArmorStand> entry : frozenPlayers.entrySet()) {
      Player p = entry.getKey();
      FakeArmorStand s = entry.getValue();
      s.destroy(p);
    }
    frozenPlayers.clear();
    HandlerList.unregisterAll(listener);
  }

  /**
   * Check if a player is currently frozen.
   *
   * @param player to check
   * @return if the player is frozen
   */
  public boolean isFrozen(Player player) {
    return frozenPlayers.keySet().contains(player);
  }

  /**
   * Freeze a player in their current location.
   *
   * @param player to freeze
   */
  public void freeze(Player player) {
    if (isFrozen(player)) {
      return;
    }

    FakeArmorStand stand = new FakeArmorStand(player.getWorld());
    stand.spawn(player, player.getLocation());
    stand.mount(player, player);
    frozenPlayers.put(player, stand);
  }

  /**
   * Unfreeze a player.
   *
   * @param player to thaw
   */
  public void thaw(Player player) {
    FakeArmorStand stand = frozenPlayers.remove(player);
    if (stand != null) {
      stand.destroy(player);
    }
  }
}
