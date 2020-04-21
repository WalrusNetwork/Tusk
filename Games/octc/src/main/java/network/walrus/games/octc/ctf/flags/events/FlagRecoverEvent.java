package network.walrus.games.octc.ctf.flags.events;

import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.octc.ctf.flags.FlagObjective;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a dropped flag has been returned to it's original post.
 *
 * @author Austin Mayes
 */
public class FlagRecoverEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();

  /** @param objective that was recovered */
  public FlagRecoverEvent(FlagObjective objective) {
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
