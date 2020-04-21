package network.walrus.games.core.events.objective;

import network.walrus.games.core.facets.objectives.Objective;
import org.bukkit.event.HandlerList;

/**
 * An event that is the superclass to all events that are related to objectives changing from one
 * state to the next.
 *
 * @author Avicus Network
 */
public class ObjectiveStateChangeEvent extends ObjectiveEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  /**
   * Constructor.
   *
   * @param objective objective that changed
   */
  public ObjectiveStateChangeEvent(Objective objective) {
    super(objective);
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  public static HandlerList getHandlerList() {
    return handlers;
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
