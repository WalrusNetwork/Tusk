package network.walrus.ubiquitous.bukkit.countdown;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event which is called when a {@link Countdown} is canceled.
 *
 * @author Avicus Network
 */
public class CountdownCancelEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private final Countdown canceled;

  /** @param canceled countdown which was canceled */
  public CountdownCancelEvent(Countdown canceled) {
    this.canceled = canceled;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public Countdown getCanceled() {
    return canceled;
  }
}
