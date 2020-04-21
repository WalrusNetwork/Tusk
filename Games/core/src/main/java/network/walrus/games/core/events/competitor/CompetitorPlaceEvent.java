package network.walrus.games.core.events.competitor;

import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Competitor} places in a {@link GameRound}
 *
 * @author Austin Mayes
 */
public class CompetitorPlaceEvent extends Event implements Cancellable {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** Round that the competitor placed in. */
  private final GameRound round;
  /** Competitor that has placed. */
  private final Competitor competitor;
  /** Ranking of the competitor. */
  private final int place;
  /** If the event was canceled. */
  private boolean cancelled;

  /**
   * Constructor.
   *
   * @param round round that the competitor placed in
   * @param competitor competitor that has placed
   * @param place ranking of the competitor
   */
  public CompetitorPlaceEvent(GameRound round, Competitor competitor, int place) {
    this.round = round;
    this.competitor = competitor;
    this.place = place;
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

  public Competitor getCompetitor() {
    return competitor;
  }

  public int getPlace() {
    return place;
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
