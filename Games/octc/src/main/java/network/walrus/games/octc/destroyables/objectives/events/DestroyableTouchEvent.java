package network.walrus.games.octc.destroyables.objectives.events;

import network.walrus.games.core.events.objective.ObjectiveTouchEvent;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a {@link DestroyableObjective} is touched by a player.
 *
 * @author Austin Mayes
 */
public class DestroyableTouchEvent extends ObjectiveTouchEvent {

  private static final HandlerList handlers = new HandlerList();

  /**
   * @param objective objective that was touched
   * @param info about what caused the event
   */
  public DestroyableTouchEvent(DestroyableObjective objective, DestroyableEventInfo info) {
    super(objective, info.getActor());
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
