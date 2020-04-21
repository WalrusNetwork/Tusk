package network.walrus.games.core.events.round;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link GameRound} is closed.
 *
 * @author Austin Mayes
 */
public class RoundCloseEvent extends RoundEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  /**
   * Constructor.
   *
   * @param round round that is closed
   */
  public RoundCloseEvent(FacetHolder round) {
    super(round);
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
