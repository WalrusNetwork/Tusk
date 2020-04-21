package network.walrus.nerve.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PermissionsLoadedEvent extends Event {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  private final Player player;

  /**
   * Constructor.
   *
   * @param player
   */
  public PermissionsLoadedEvent(Player player) {
    this.player = player;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
