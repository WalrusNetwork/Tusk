package network.walrus.games.core.events.competitor;

import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Competitor} wins a {@link GameRound}
 *
 * @author Avicus Network
 */
public class CompetitorWinEvent extends Event implements Cancellable {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** Round that the competitor won. */
  private final FacetHolder round;
  /** Competitor that won the round. */
  private final Competitor winner;
  /** If the event was canceled. */
  private boolean cancelled;

  /**
   * @param round round that the competitor won
   * @param winner competitor that won the round
   */
  public CompetitorWinEvent(FacetHolder round, Competitor winner) {
    this.round = round;
    this.winner = winner;
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

  public FacetHolder getRound() {
    return round;
  }

  public Competitor getWinner() {
    return winner;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
}
