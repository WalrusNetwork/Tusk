package network.walrus.ubiquitous.bukkit.listeners;

import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.events.player.PlayerJoinDelayedEvent;
import network.walrus.utils.bukkit.listener.EventUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listener which holds some miscellaneous handlers needed to make various systems work. This is
 * used when certain handlers don't logically fit anywhere else.
 *
 * @author Austin Mayes
 */
public class GeneralListener implements Listener {

  /** Fire PlayerJoinDelayedEvent and cancel default join message */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    event.setJoinMessage(null);

    new BukkitRunnable() {
      @Override
      public void run() {
        if (event.getPlayer().isOnline()) {
          PlayerJoinDelayedEvent call = new PlayerJoinDelayedEvent(event.getPlayer());
          EventUtil.call(call);
        }
      }
    }.runTaskLater(UbiquitousBukkitPlugin.getInstance(), 1);
  }

  /** Cancel default death message */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    event.setDeathMessage(null);
  }

  /** Cancel default leave message */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    event.setQuitMessage(null);
  }

  /** Cancel default kick message */
  @EventHandler
  public void onPlayerKick(PlayerKickEvent event) {
    event.setLeaveMessage(null);
  }
}
