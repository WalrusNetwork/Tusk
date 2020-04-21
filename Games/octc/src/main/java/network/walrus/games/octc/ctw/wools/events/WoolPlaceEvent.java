package network.walrus.games.octc.ctw.wools.events;

import network.walrus.games.core.events.objective.ObjectiveCompleteEvent;
import network.walrus.games.octc.ctw.wools.WoolObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event called when a {@link Player} places a {@link WoolObjective} in the correct location.
 *
 * @author Austin Mayes
 */
public class WoolPlaceEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();

  /**
   * @param objective which is being placed
   * @param player that placed the wool
   */
  public WoolPlaceEvent(WoolObjective objective, Player player) {
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
