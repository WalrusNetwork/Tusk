package network.walrus.games.octc.ctf.flags.events;

import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.octc.ctf.flags.FlagObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a player picks up a flag that isn't resting at a post.
 *
 * @author Austin Mayes
 */
public class FlagPickupEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;

  /**
   * @param objective being picked up
   * @param player who picked up the flag
   */
  public FlagPickupEvent(FlagObjective objective, Player player) {
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
