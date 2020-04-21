package network.walrus.utils.bukkit.visual.display;

import network.walrus.utils.bukkit.visual.Sidebar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Simple API to manage every {@link DisplayPane} linked to a {@link Player} and adds mass-update
 * functionality.
 *
 * @author Austin Mayes
 */
public interface DisplayManager {

  /**
   * Clear all of the sidebars from the reference list. This should only be used in order to force
   * new {@link Sidebar}s to be created for every single player.
   */
  void clearBars();

  /**
   * Set the current global scoreboard frame.
   *
   * @param frame the new global frame
   */
  void setFrame(DisplayPane frame);

  /**
   * Purge any data relating to the player from the cache of the player's current frame.
   *
   * @param player to purge data for
   */
  void updateCache(Player player);

  /**
   * Purge all elements in the cache for the frame, render the updated versions, and populate the ID
   * map to make sure all elements are identified.
   *
   * @param frame to update elements for
   */
  void updateElements(DisplayPane frame);

  /**
   * Update all elements on all frames with the given ID and then render the results.
   *
   * @param id to update
   */
  void update(String id);

  /** Update all elements being displayed to the player and render the results. */
  void update(Player player);

  /**
   * Update an renderable based on ID in the context of a specific player and render the results.
   *
   * @param player to send the updated results to
   * @param id to update
   */
  void update(Player player, String id);

  /**
   * Get the current frame a player is using. This will never return null, and will fallback to the
   * global frame if none is specified.
   *
   * @param player to get the frame for
   * @return the current frame a player is using
   */
  DisplayPane getPlayerFrame(Player player);

  /**
   * Set the scoreboard frame for a certain player, it will be shown in lieu of the global
   * scoreboard.
   *
   * @param frame the new frame for the player
   * @param player the player to get the new frame
   */
  void setFrame(DisplayPane frame, Player player);

  /**
   * Get the {@link Sidebar} that a player is using. If the player doesn't have one, one will be
   * created and assigned to them.
   *
   * @param player to get the bar for
   * @return the sidebar that a player is using
   */
  Sidebar getSidebar(Player player);

  /** Main plugin which is responsible for registering listeners and running tasks. */
  Plugin owner();

  /**
   * Called when the owning plugin is enabled. This is responsible for handling any initial setup
   * that the manager might need in order to be ready for use.
   */
  void init();
}
