package network.walrus.games.octc.ctf.flags.events;

import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.octc.ctf.flags.FlagObjective;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a {@link FlagObjective} spawns at a post.
 *
 * @author Austin Mayes
 */
public class FlagSpawnEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();

  /** @param objective which spawned */
  public FlagSpawnEvent(FlagObjective objective) {
    super(objective);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
