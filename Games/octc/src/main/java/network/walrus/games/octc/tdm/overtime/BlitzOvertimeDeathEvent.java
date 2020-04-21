package network.walrus.games.octc.tdm.overtime;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event that is called when a player dies during the Blitz Overtime event for TDM
 *
 * @author David Rodriguez
 */
public class BlitzOvertimeDeathEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();

  /** @param who died during overtime */
  public BlitzOvertimeDeathEvent(Player who) {
    super(who);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
