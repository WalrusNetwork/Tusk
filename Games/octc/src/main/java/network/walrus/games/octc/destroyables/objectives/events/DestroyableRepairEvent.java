package network.walrus.games.octc.destroyables.objectives.events;

import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a {@link DestroyableObjective} is repaired by a player.
 *
 * @author Austin Mayes
 */
public class DestroyableRepairEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;

  /**
   * @param objective objective that changed
   * @param player who is repairing the objective
   */
  public DestroyableRepairEvent(DestroyableObjective objective, Player player) {
    super(objective);
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }
}
