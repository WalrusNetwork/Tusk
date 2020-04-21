package network.walrus.games.octc.ctf.flags.events;

import network.walrus.games.core.events.objective.ObjectiveCompleteEvent;
import network.walrus.games.octc.ctf.flags.FlagObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a {@link FlagObjective} is successfully returned to a net.
 *
 * @author Austin Mayes
 */
public class FlagCaptureEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();

  /**
   * @param player who captured the flag
   * @param objective being captured
   */
  public FlagCaptureEvent(Player player, FlagObjective objective) {
    super(objective, player);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
