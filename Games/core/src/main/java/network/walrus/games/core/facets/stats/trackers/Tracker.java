package network.walrus.games.core.facets.stats.trackers;

import java.util.UUID;
import network.walrus.games.core.GamesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Wrapper class for statistic trackers so they get registered automatically.
 *
 * @author Rafi Baum
 */
public abstract class Tracker<T> implements Listener {

  public Tracker() {
    Bukkit.getPluginManager().registerEvents(this, GamesPlugin.instance);
  }

  public void unload() {
    HandlerList.unregisterAll(this);
  }

  /**
   * Get the value of the player's contribution
   *
   * @param uuid of the player
   * @return player's contribution
   */
  public abstract double getScore(UUID uuid);

  /**
   * Get the value of the player's contribution
   *
   * @param player
   * @return player's contribution
   */
  public double getScore(Player player) {
    return getScore(player.getUniqueId());
  }

  /**
   * Fetches uncached changes and assumes all content is now cached.
   *
   * @param player
   * @return uncached stats diff
   */
  public abstract T fetchUpdate(Player player);

  /**
   * Reset player's contribution
   *
   * @param uuid of the player to reset
   */
  public abstract void reset(UUID uuid);

  /**
   * Reset player's contribution
   *
   * @param player to reset
   */
  public void reset(Player player) {
    reset(player.getUniqueId());
  }
}
