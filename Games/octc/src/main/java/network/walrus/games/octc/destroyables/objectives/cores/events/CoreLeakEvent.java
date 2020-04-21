package network.walrus.games.octc.destroyables.objectives.cores.events;

import network.walrus.games.core.events.objective.ObjectiveCompleteEvent;
import network.walrus.games.octc.destroyables.objectives.cores.CoreObjective;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableEventInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event called when a {@link Player} places a {@link CoreObjective} in the correct location.
 *
 * @author ShinyDialga
 */
public class CoreLeakEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();
  private final DestroyableEventInfo info;

  /**
   * @param objective objective involved in the event
   * @param info info about the event
   */
  public CoreLeakEvent(CoreObjective objective, DestroyableEventInfo info) {
    super(objective, info.getActor());
    this.info = info;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public CoreObjective getObjective() {
    return (CoreObjective) super.getObjective();
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
