package network.walrus.games.core.events.round;

import java.util.Collection;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link GameRound} is completed with winner(s). This is not called
 * during ties.
 *
 * @author Austin Mayes
 */
public class RoundCompleteEvent extends RoundEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  private final Collection<? extends Competitor> competitors;
  private final Collection<Competitor> winners;

  /**
   * Constructor.
   *
   * @param holder round that was opened
   * @param competitors who played in the round.
   * @param winners who won the round.
   */
  public RoundCompleteEvent(
      FacetHolder holder,
      Collection<? extends Competitor> competitors,
      Collection<Competitor> winners) {
    super(holder);
    this.competitors = competitors;
    this.winners = winners;
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

  public Collection<? extends Competitor> getCompetitors() {
    return competitors;
  }

  public Collection<Competitor> getWinners() {
    return winners;
  }
}
