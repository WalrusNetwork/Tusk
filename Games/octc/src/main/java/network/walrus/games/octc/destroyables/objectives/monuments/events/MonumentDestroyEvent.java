package network.walrus.games.octc.destroyables.objectives.monuments.events;

import network.walrus.games.core.events.objective.ObjectiveCompleteEvent;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableEventInfo;
import network.walrus.games.octc.destroyables.objectives.monuments.MonumentObjective;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a {@link MonumentObjective} is destroyed by a player.
 *
 * @author Austin Mayes
 */
public class MonumentDestroyEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();
  private final DestroyableEventInfo info;

  /**
   * @param objective objective involved in the event
   * @param info info about the event
   */
  public MonumentDestroyEvent(MonumentObjective objective, DestroyableEventInfo info) {
    super(objective, info.getActor());
    this.info = info;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public MonumentObjective getObjective() {
    return (MonumentObjective) super.getObjective();
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public DestroyableEventInfo getInfo() {
    return info;
  }
}
