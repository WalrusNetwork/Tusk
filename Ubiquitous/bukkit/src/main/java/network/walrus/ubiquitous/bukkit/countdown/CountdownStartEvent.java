package network.walrus.ubiquitous.bukkit.countdown;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event which is called when a {@link Countdown} is started.
 *
 * @author Avicus Network
 */
public class CountdownStartEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private final Countdown started;

  /** @param started countdown which was started */
  public CountdownStartEvent(Countdown started) {
    this.started = started;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public Countdown getStarted() {
    return started;
  }
}
