package network.walrus.ubiquitous.bukkit.settings;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a setting if modified by a player.
 *
 * @author Avicus Network
 */
public class SettingModifyEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private final SettingContext setting;
  private final Player player;

  /**
   * Constructor.
   *
   * @param setting context containing the setting being modified and the new value of the setting
   * @param player who is modifying the setting
   */
  SettingModifyEvent(SettingContext setting, Player player) {
    this.setting = setting;
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public SettingContext getSetting() {
    return setting;
  }

  public Player getPlayer() {
    return player;
  }
}
