package network.walrus.games.octc.destroyables.objectives.events;

import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a {@link DestroyableObjective} is damaged by a player.
 *
 * @author Austin Mayes
 */
public class DestroyableDamageEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  private final DestroyableEventInfo info;

  /**
   * @param objective objective that changed
   * @param info describing the cause of the event
   */
  public DestroyableDamageEvent(DestroyableObjective objective, DestroyableEventInfo info) {
    super(objective);
    this.info = info;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public DestroyableEventInfo getInfo() {
    return info;
  }
}
