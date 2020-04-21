package network.walrus.games.core.events.objective;

import network.walrus.games.core.facets.objectives.Objective;
import org.bukkit.event.Event;

/**
 * An event that is the superclass to all events that are related to objectives.
 *
 * @author Avicus Network
 */
public abstract class ObjectiveEvent extends Event {

  /** The objective involved in the event. */
  private final Objective objective;

  /**
   * Constructor,
   *
   * @param objective objective involved in the event
   */
  protected ObjectiveEvent(Objective objective) {
    this.objective = objective;
  }

  public Objective getObjective() {
    return objective;
  }
}
