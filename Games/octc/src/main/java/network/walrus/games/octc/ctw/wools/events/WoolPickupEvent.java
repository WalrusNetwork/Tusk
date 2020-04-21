package network.walrus.games.octc.ctw.wools.events;

import network.walrus.games.core.events.objective.ObjectiveTouchEvent;
import network.walrus.games.octc.ctw.wools.WoolObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a {@link Player} succesfully picks up a {@link WoolObjective}.
 *
 * @author Austin Mayes
 */
public class WoolPickupEvent extends ObjectiveTouchEvent {

  private static final HandlerList handlers = new HandlerList();

  /**
   * @param objective being picked up
   * @param player who picked up the objective
   */
  public WoolPickupEvent(WoolObjective objective, Player player) {
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
