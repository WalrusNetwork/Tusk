package network.walrus.games.octc.hills.overtime;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event called when a game enters the overtime stage
 *
 * @author Matthew Arnold
 */
public class OvertimeStartEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
