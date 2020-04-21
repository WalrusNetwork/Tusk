package network.walrus.games.core.facets.objectives.locatable;

import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.core.facets.objectives.Objective;
import org.bukkit.event.HandlerList;

/**
 * Event that is fired every time the calculated distance is updated for a {@link
 * LocatableObjective}.
 *
 * @author Austin Mayes
 */
public class LocatableUpdateDistanceEvent extends ObjectiveStateChangeEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  /** @param objective which is being updated */
  public LocatableUpdateDistanceEvent(Objective objective) {
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
