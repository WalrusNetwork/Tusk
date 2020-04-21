package network.walrus.games.core.events.round;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link GameRound} is opened to players.
 *
 * @author Austin Mayes
 */
public class RoundOpenEvent extends RoundEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  /**
   * Constructor.
   *
   * @param holder round that was opened
   */
  public RoundOpenEvent(FacetHolder holder) {
    super(holder);
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
