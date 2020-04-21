package network.walrus.games.core.events.group;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event which is fired whenever a player changes observer state.
 *
 * @author Rafi Baum
 */
public class PlayerObserverStateChangeEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();
  private final boolean toObserver;

  /**
   * Constructor.
   *
   * @param who is changing state
   * @param toObserver whether the player is changing to observer status or from observer status
   */
  public PlayerObserverStateChangeEvent(Player who, boolean toObserver) {
    super(who);

    this.toObserver = toObserver;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  /** @return if the player is becoming an observer */
  public boolean isToObserver() {
    return toObserver;
  }

  /** @return if the player is losing observer status */
  public boolean isFromObserver() {
    return !toObserver;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
