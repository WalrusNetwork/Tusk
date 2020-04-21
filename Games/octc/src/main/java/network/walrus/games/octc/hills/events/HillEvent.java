package network.walrus.games.octc.hills.events;

import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.octc.hills.HillObjective;

/**
 * A super class of all hill the different hill events
 *
 * @author Matthew Arnold
 */
public abstract class HillEvent extends ObjectiveStateChangeEvent {

  protected final HillObjective hillObjective;

  /**
   * Creates a new hill event
   *
   * @param objective the hill objective
   */
  public HillEvent(HillObjective objective) {
    super(objective);
    this.hillObjective = objective;
  }

  /**
   * Gets the hill that this event is referencing
   *
   * @return the hill
   */
  public HillObjective getObjective() {
    return hillObjective;
  }
}
